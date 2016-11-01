export test1,test2,test3,machine,DET,run,NONDET,test4,COUNT, test5

import 'src/xpl/exp.xpl'
import 'src/xpl/lists.xpl'

listOfExpToExp(l) =
  case l {
    []  -> [| [] |];
    e:l -> [| ${e}:${listOfExpToExp(l)} |]
  }

exp = {
  syntax(semantics) = {
    arith      -> whitespace a=atom tail^(a);
    atom       -> i=int | '(' a=arith ')' { a };
    whitespace -> (32 | 10 | 9 | 13)*;  
    int        -> n=(['0','9'])+ { semantics.int(n) };
    tail(left) -> o=op right=arith { semantics.binExp(left,o,right) } | { left };
    op         -> whitespace ('/' | '*')
  };
  semantics = {
    binExp(left,op,right) = BinExp(left,op,right);
    int(cs)               = Int(cs)
  }
}
  
actions(exp) = {
  syntax(semantics) = {
    action -> block | update;
    block -> '{' as=actions '}' { semantics.block(as) };
    actions -> a=action as=(';' action)* { a:as };
    actions -> { [] };
    update -> n=name whitespace ':=' e=exp { semantics.update(n,e) };
    whitespace -> (32 | 10 | 9 | 13)*;  
    name   -> whitespace not('machine') not('on') l=alpha ls=alphaNumeric* { asString(l:ls) };
    alpha  -> ['a','z'];
    alpha  -> ['A','Z'];
    numeric -> ['0','9'];
    alphaNumeric -> alpha | numeric
  };
  semantics = {
    update(n,e) = [| fun(r) recordUpdate(r,${n.lift()},${e}) |];
    block(as) = 
      case as {
        [] -> [| fun(r) r |];
        a:as -> [| fun(r) ${((actions(exp)).semantics).block(as)}(${a}(r)) |]
      }
  }
}
    

stm(exp,body) = {
  syntax(semantics) = {
    stm -> 'machine' whitespace '(' s = states ')' whitespace '{' t = transs '}' { semantics.machine(s,t) };
    states -> s = name ss = (',' name)* { s:ss };
    states -> { [] };
    transs -> t = trans ts = (';' trans)* { t: ts };
    transs -> { [] };
    trans  -> whitespace 'on' e=name whitespace '(' as=args ')' 'in' s=name 'when' c=condition '->' t=name 'do' a=body { semantics.trans(e,as,s,c,t,a) };
    args   -> n=name ns=(',' name)* {n:ns};
    args   -> { [] };
    condition -> 'true' { [| true |] };
    action -> 'nothing' { [| fun() null |] };
    whitespace -> (32 | 10 | 9 | 13)*;  
    name   -> whitespace not('machine') not('on') l=alpha ls=alphaNumeric* { asString(l:ls) };
    alpha  -> ['a','z'];
    alpha  -> ['A','Z'];
    numeric -> ['0','9'];
    alphaNumeric -> alpha | numeric
  };
  semantics = {
    machine(s,t) = [| 
      letrec machine(s,v) = { 
        self = machine;
        state = s;
        vars = v;
        states=${s.lift()};
        trans=${listOfExpToExp(t)}
      }
      in machine(head(${s.lift()}),{}) 
    |];
    trans(e,as,s,c,t,a) = [| 
      fun(m,e,as,k)
        if e=${e.lift()} and ${s.lift()}=m.state
        then 
          if ${c}
          then print(m.self(${t.lift()},${a}(m.vars)))
          else k()
        else k()
    |]
  }
}

send(m,e,vs) =
  letrec next(ts) =
    case ts {
      [] -> m;
      t:ts ->
        t(m,e,vs,fun() next(ts))
    }
  in next(m.trans)

test1() = 
  let EXP = exp.syntax(exp.semantics) in
  let ACTIONS = (actions(EXP)).syntax((actions(EXP)).semantics) in
  let STM = stm(EXP,ACTIONS) in
  let machine = 
    intern STM.syntax(STM.semantics) {
      machine(s1,s2) {
        on e1() in s1 when true -> s2 do {
          x:=10;
          y:= 20
        };
        on e2() in s2 when true -> s1 do {}
      }
    }
  in send(send(machine,'e1',[]),'e2',[])
  
DET(m) = {
  new(next) = next(head(m.states));
  stop(state)=state;
  transition(p,next,alt) =
    let trans = filter(p,m.trans)
    in case trans {
         [] -> alt;
         t:ts -> next(t)
       };
  become(state,next) = fun(ignore) next(state);
  state(f) = fun(state) (f(state))(state)
}
  
PRIO(m) = {
  new(next) = next(head(m.states));
  stop(state)=state;
  transition(p,next,alt) =
    let trans = sort(fun(t1,t2) t1.tag < t2.tag,filter(p,m.trans))
    in case trans {
         [] -> alt;
         t:ts -> next(t)
       };
  become(state,next) = fun(ignore) next(state);
  state(f) = fun(state) (f(state))(state)
}
  
NONDET(m) = {
  new(next) = next(head(m.states));
  stop(state)=[state];
  transition(p,next,alt) =
    let trans = filter(p,m.trans)
    in case trans {
         [] -> alt;
         ts -> fun(state) 
           let funs = map(next,ts)
           in flatten(map(fun(f) f(state),funs))
       };
  become(state,next) = fun(ignore) next(state);
  state(f) = fun(state) (f(state))(state)
}
  
COUNT(m) = {
  new(next) = next({state=head(m.states);count=0});
  stop(state)=[state];
  transition(p,next,alt) =
    let trans = filter(p,m.trans)
    in case trans {
         [] -> alt;
         ts -> fun(state) 
           let funs = map(next,ts)
           in flatten(map(fun(f) f(state),funs))
       };
  become(state,next) = fun(previous) next({state=state;count=previous.count + 1});
  state(f) = fun(state) (f(state.state))(state)
}
  
run(m,es) =
  import m {
    letrec eval(es) =
      case es {
        [] -> stop;
        e:es ->
          state(fun(s) 
            transition(fun(t) s = t.source and e = t.event,
                       fun(t) become(t.target,eval(es)),
                       eval(es)))
      }
    in new(eval(es))
  }
  
machine(tag) = {
  machine -> 'machine' '(' m=name ')' ss=states '{' t=trans* '}' { [| ${Var(m)}({states=${listOfExpToExp(ss)};trans=${listOfExpToExp(t)}}) |] };
  states -> '[' s=name ss=(',' name)* ']' { map(fun(s) s.lift(),s:ss) };
  trans -> s=name e=event t=name whitespace x=tag { [| {source=${s.lift()};event=${e.lift()};target=${t.lift()};tag=${x}} |] };
  event -> '-(' n=name ')->' { n };
  whitespace -> (32 | 10 | 9 | 13)*;   
  name   -> whitespace not('machine') not('on') l=alpha ls=alphaNumeric* { asString(l:ls) };
  alpha  -> ['a','z'];
  alpha  -> ['A','Z'];
  numeric -> ['0','9'];
  alphaNumeric -> alpha | numeric
}

nothingTag = { tag -> { [| 'nothing' |] } }

priortyTag = { tag -> 'priority(' n = ['0','9'] ')' { n.lift() } }
    
m1 = {
  states=['s1','s2','s3'];
  trans=[
    {source='s1';event='e1';target='s2'},
    {source='s2';event='e2';target='s3'},
    {source='s2';event='e2';target='s4'},
    {source='s3';event='e3';target='s3'},
    {source='s3';event='e4';target='s1'},
    {source='s3';event='e4';target='s2'}
  ]}
  
test4() = 
  let m = intern machine(nothingTag) {
            machine(COUNT) [s1,s2,s3] {
              s1-(e1)->s2
              s2-(e2)->s1
              s2-(e2)->s3
              s3-(e3)->s1
            }
          }
  in run(m,['e1','e2','e1'])
  
test5() = 
  let m = intern machine(priortyTag) {
            machine(PRIO) [s1,s2,s3] {
              s1-(e1)->s2 priority(1)
              s2-(e2)->s1 priority(3)
              s2-(e2)->s3 priority(2)
              s2-(e3)->s1 priority(2)
            }
          }
  in run(m,['e1','e2','e1'])