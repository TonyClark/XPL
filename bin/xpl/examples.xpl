export 
  example1, example2, example3, example4, example5, example6, 
  example7, example8, example9, example10, example11, example12,
  example13, example14, example15, example16, example17, example18,
  example19, example20, example21, example22, example23, example24,
  example25, example26, example27, example28,example29, example30,
  example31, example32, example33, example33,example34, example35,
  example36, example37, example38, example39, example40, example41,
  example42, example43, example44, example45, example46, example47,
  example48, example49, example50, example51, example52, example53,
  example54, example55, example56, example57, example58, example59,
  example60, example61, example62, example63, example64, example65,
  example66, example67, example68, example69, example70, example71,
  example72, example73, example74, example75, example76, example77,
  example78, example79, example80, example81, example82, example83,
  example84, example85, example86, example87, example88, example89,
  example90

import 'src/xpl/exp.xpl'
import 'src/xpl/xpl.xpl'
import 'src/xpl/lists.xpl'

// Examples from Basic XML

length(l) = if l = [] then 0 else 1 + length(tail(l))

example1() = length([0,'one',true])

addvec(v1,v2) = { x = v1.x + v2.x; y = v1.y + v2.y }

example2() = addvec({x=1;y=2},{x=3;y=4})

example3() = import {x=1} { x + 1} 

example4() =
  let r = {x=1}
      x = 2
  in import r { x }
  
// Examples from Syntax Construction

example5() =
  case If(BinExp(Var('l'),'=',List([])),Var('x'),Var('y')) { 
    If(t,c,a) -> c 
  } 
  
example6() =
  case [| if l = [] then x else y |] { 
    If(t,c,a) -> c 
  } 
  
example7() = [| 10 |].eval({}) 

example8() = [| x |].eval({x=100}) 

example9() =  [| if l = [] then x else y |].eval({l=[];x=1;y=2}) 

example10() = [| if l = [] then x else y |].eval({l=['dog'];x=1;y=2}) 

createAccessor(name) = [| fun(record) record.${name} |]

example11() = createAccessor('x') 

example12() = createAccessor('y') 

// Examples from Syntax Transformation

add1Exp(x) = [| [ ${ x } + 1 ] |]

appExps(e1,e2) = [| ${ e1 } + ${ e2 } |]

foldr(f,g,b,l) =
  case l {
    [] -> b;
    h:t -> g(f(h),foldr(f,g,b,t))
  }
  
example13() =  foldr(fun(x) [x+1],fun(l1,l2) l1 + l2,[],[100,200])

consolidate(exps) = foldr(add1Exp,appExps,[| [] |],exps)

example14() = consolidate([]) 

example15() = consolidate([ [| a |], [| b |] ]) 

example16() = (consolidate([ [| a |], [| b |] ])).eval({a=100;b=200})

// Examples from Grammars

g1 = { start -> 'x' { 10 } }

example17() = g1.parse('x',[]) 

example18() =  g1.parse('y',[]) 

g2 = { start -> 'x' { 10 } | 'y' { 20 } }

example19() =  g2.parse('x',[]) 

example20() =  g2.parse('y',[]) 

g3 = { start -> (getx | gety)*;
      getx  -> 'x' { 10 };
      gety  -> 'y' { 20 } }
      
example21() = g3.parse('xyxyxyxy',[])

g4 = { start   -> (getx | gety)*;
      getx    -> spaces 'x' { 10 };
      gety    -> spaces 'y' { 20 };
      spaces  -> (32 | 10 | 13 | 9)* }
      
example22() = g4.parse(' x    yxy  xy x      y',[]) 

g5 = { start(n) -> m=(getx | gety) start^(n+m) | { n };
       getx     -> spaces 'x' { 10 };
       gety     -> spaces 'y' { 20 };
       spaces   -> (32 | 10 | 13 | 9)* }
       
example23() = g5.parse('',[0]) 

example24() = g5.parse('x',[0]) 

example25() = g5.parse('x x y x y',[0]) 

example26() = g5.parse('x x y x y',[9]) 

less(n,m) = n < m

eql(n,m)  = n = m

range     = {
  start      -> '[' low=num ',' high=num ']' range^(low,high);
  range(n,m) -> ?less(n,m) { {low=n;high=m} } | ?eql(n,m) {n} | {'error'};
  num        ->  i=[48,57]+ { asInt(i) }
}

example27() = range.parse('[100,200]',[]) 

example28() = range.parse('[100,100]',[]) 

example29() = range.parse('[100,20]',[])

g6 = { start1 -> 'x' { 10 } }
g7 = { start2 -> 'y' { 20 } }
g8 = { start  -> 'x' { 10 } }
g9 = { start  -> 'y' { 20 } }

example30() = (g6+g7).parse('x',[]) 

example31() = (g6+g7).parse('y',[]) 

example32() = (g7+g6).parse('x',[]) 

example33() = (g7+g6).parse('y',[]) 

example34() = (g8+g9).parse('x',[]) 

example35() = (g8+g9).parse('y',[]) 

example36() = (g9+g8).parse('x',[]) 

example37() = (g9+g8).parse('y',[]) 

// Examples from section Languages

xory = { start -> 'x'* { [| true |] } | 'y'* { [| false |] } }

example38() = intern xory { xxxx }

example39() = intern xory { yyyyy }

parse(g) = intern g { xxx }

example40() = parse({ start -> 'x'* { [| true |] } }) 

example41() = parse({ start -> 'x'* { [| false |] } })

// Examples from section A Language for Libraries

fields = XPL.fields

library = { 
  library -> h = header fs = fields  { 
    [| { header = ${h.lift()}; defs = ${Record(fs)} } |] 
  }; 
  header -> 'header' s=.* 'end' { asString(s) }
}

example42() =
  intern library {
    header
      This library performs arithmetic.
    end
    add(n,m) = n + m;
    sub(n,m) = n - m
  }
  
// Examples from section An Expression Language

arithExternal = {
  start      -> a=atom tail^(a);
  atom       -> int | '(' a=start ')' { a };
  int        -> n=(['0','9'])+ { asInt(n) };
  tail(left) -> o=op right=start { 
    case o {
      '*' -> left * right;
      '/' -> left / right
    }
  };
  tail(left) -> { left };
  op         -> ('/' | '*')
}

example43() = arithExternal.parse('10*20',[])

arithInternal = {
  start      -> a=atom tail^(a);
  atom       -> int | '(' a=start ')' { a };
  int        -> n=(['0','9'])+ { Int(asInt(n)) };
  tail(left) -> o=op right=start { 
    case o {
      '*' -> [| ${left} * ${right} |];
      '/' -> [| ${left} / ${right} |]
    }
  };
  tail(left) -> { left };
  op         -> ('/' | '*')
}

example44() = intern arithInternal {10*20}

example45() = intern arithInternal {20*10}

example46() = intern arithInternal {(10/2)*(20/4)}

// Examples from section Processing Input Streams

combine(left,right) = [| fun(l) ${left}(l,fun(r,l) r + ${right}(l)) |]

id(x) = x

empty = [| fun(l) {} |]

extractor(n,i) = 
  let record = [| { ${n} = asString(take(l,${i})) } |]
  in [| fun(l,k) k(${record},drop(l,${i})) |]
  
inflator = {
  fields -> fs=field* { foldr(id,combine,empty,fs) };
  field -> n=name spaces ':' i=int { extractor(n,i) };
  int -> spaces n=numeric+ { Int(asInt(n)) };
  spaces -> (32 | 10 | 9 | 13)*;  
  name   -> spaces l=alpha ls=alpha* { asString(l:ls) };  
  alpha  -> ['a','z'];
  numeric -> ['0','9']
}

customer = 
  intern inflator {
    customer:5
    address:15
    account:3
  }
 
input = [102,114,101,100,32,49,48,32,77,97,105,110,32,82,111,97,100,32,32,32,53,48,49]
  
example47() = customer(input)

example48() = map(customer,repeat(input,10))

// Examples from section Guarded Command Language

begin(c) = c([],fun(state) state)

seq(c1,c2) = fun(state,continuation) c1(state,fun(state) c2(state,continuation))

update(name,value) = fun(state,continuation) continuation({name=name;value=value(state)}:state)

ref(name) = fun(state) case state { r:s -> if r.name=name then r.value else (ref(name))(s); [] -> '?' }

abort(n) = fun(state,continuation) (ref(n))(state)

bin(left,op,right) = fun(state)
  case op {
    '>' -> left(state) > right(state);
    '<' -> left(state) < right(state);
    '+' -> left(state) + right(state);
    '-' -> left(state) - right(state)
  }
  
cond(arm) = fun(state,continuation)
  arm(state,continuation,fun()continuation(state))
  
do(arm) = fun(state,continuation)
  arm(state,fun(state) (do(arm))(state,continuation),fun()continuation(state))
  
alt(a1,a2) = fun(state,success,fail)
  a1(state,success,fun() a2(state,success,fail))
  
try(exp,command) = fun(state,success,fail)
  if exp(state)
  then command(state,success)
  else fail()
  
gcl = {
  program  -> cs=commands { [| begin(${cs}) |] };
  commands -> c1=command (c2=commands { [| seq(${c1},${c2}) |] } | { c1 });
  command  -> update | abort | cond | do;
  update   -> n=name spaces ':=' e=exp { [| update(${n.lift()},${e}) |] };
  abort    -> spaces 'abort' n=name { [| abort(${n.lift()}) |] };
  cond     -> spaces 'if' as=arms spaces 'fi' { [| cond(${as}) |] };
  do       -> spaces 'do' as=arms spaces 'od' { [| do(${as}) |] };
  arms     -> a1=arm (a2=arms { [| alt(${a1},${a2}) |] } | { a1 });
  arm      -> g=exp spaces '->' c=command { [| try(${g},${c}) |] };
  exp      -> a=atom (o=op e=exp { [| bin(${a},${o.lift()},${e}) |] } | {a});
  op       -> '>' | '<' | '+' | '-';
  atom     -> int | var | extern;
  var      -> n=name { [| ref(${n.lift()}) |] };
  int      -> spaces n=numeric+ { [| fun(state) ${Int(asInt(n))} |] };
  extern   -> spaces '$' e=XPL { [| fun(state) ${e} |] };
  spaces   -> (32 | 10 | 9 | 13)*;  
  name     -> spaces l=alpha ls=alpha* { asString(l:ls) };  
  alpha    -> ['a','z'];
  numeric  -> ['0','9']
}

gcd(n,m) = 
  intern gcl { 
    a := $n
    b := $m
    do
      a < b -> b := b - a
      b < a -> a := a - b
    od
    abort a
  }
  
example49() = gcd(1000,15)

// Examples from section An Indepdendent External Language Module

arithDirect = {
  syntax(semantics) = {
    arith   -> a=atom tail^(a);
    atom    -> i=int | '(' a=arith ')' { a };
    int     -> n=(['0','9'])+ { semantics.int(n) };
    tail(l) -> o=op r=arith { semantics.binExp(l,o,r) };
    tail(l) -> { l };
    op      -> ('/' | '*')
  };
  semantics = {
    binExp(left,op,right) = 
      case op {
        '*' -> left * right;
        '/' -> left / right
      };
    int(cs) = asInt(cs)
  }
}

example50() =
  arithDirect.syntax(arithDirect.semantics).parse('10*20',[])
  
// Examples from section Changing the Semantics: Substituting Calculations for Integers


arithCalc = {
  syntax = arithDirect.syntax;
  semantics = {
    binExp(l,op,r) = {
      calc = op;
      children = [l,r];
      value = arithDirect.semantics.binExp(l.value,op,r.value)
    };
    int(cs) = {
      calc = 'int'; 
      children = []; 
      value = arithDirect.semantics.int(cs) 
    }
  }
}

example51() =
  arithCalc.syntax(arithCalc.semantics).parse('10*20',[])
  
// Examples from section Homogeneous Language Embedding

arith = {
  syntax(semantics) = {
    arith   -> a=atom tail^(a);
    atom    -> i=int | '(' a=arith ')' { a };
    int     -> n=(['0','9'])+ { semantics.int(n) };
    tail(l) -> o=op r=arith { semantics.binExp(l,o,r) };
    tail(l) -> { l };
    op      -> ('/' | '*')
  };
  semantics = {
    binExp(left,op,right) = BinExp(left,op,right);
    int(cs)               = Int(cs)
  }
}

example52() = intern arith.syntax(arith.semantics) {10*20}

rational = {
  syntax = arith.syntax;
  semantics = {
    binExp(left,op,right) = 
      case op {
        '*' -> [| { num=${left}.num * ${right}.num; 
                    den=${left}.den * ${right}.den } |];
        '/' -> rational.semantics.binExp(left,'*',
                 [| { num=${right}.den; den=${right}.num } |])
      };
    int(cs) = [| { num=${Int(cs)}; den=1 } |]
  }
}

example53() = intern rational.syntax(rational.semantics) {10/3}

example54() = rational.syntax(rational.semantics).parse('10/3',[])

//Examples from section Parametric Languages

arithOps(isOp) = {
  syntax(semantics) = {
    arith      -> a=atom tail^(a);
    atom       -> i=int | '(' a=arith ')' { a };
    int        -> n=(['0','9'])+ { semantics.int(n) };
    tail(left) -> o=. ?isOp(asString([o])) right=arith { 
      semantics.binExp(left,asString([o]),right) };
    tail(left) -> { left }
  };
  semantics = arith.semantics
}

mod1 = arithOps(fun(o) o = '*' or o = '/')

example55() = intern mod1.syntax(mod1.semantics) {10*20} 

mod2 = arithOps(fun(o) o = '+' or o = '-')

example56() = intern mod2.syntax(mod2.semantics) {10+20} 

arithOps2(op) = {
  syntax(semantics) = {
    arith      -> a=atom tail^(a);
    atom       -> i=int | '(' a=arith ')' { a };
    int        -> n=(['0','9'])+ { semantics.int(n) };
    tail(l)    -> o=op r=arith { semantics.binExp(l,o,r) };
    tail(l)    -> { l }
  };
  semantics = arith.semantics
}

op1 = { op -> '*' | '/' }

op2 = { op -> '+' | '-' }

mod3 = arithOps2(op1)

mod4 = arithOps2(op2)

lang1 = mod3.syntax(mod3.semantics)

lang2 = mod4.syntax(mod4.semantics)

example57() = intern lang1 {10*20}

example58() = intern lang2 {10+20}

example59() = intern lang1+lang2 {10*20}

example60() = intern lang1+lang2 {10+20} 

example61() = intern lang1+lang2 {10*20+30} 

example62() = intern (arithOps2(op1 + op2)).syntax((arithOps2(op1 + op2)).semantics) {10+20*30}

// Examples from section Extensible Languages

spaces = { spaces -> (32 | 10 | 9 | 13)* }

arithBase = {
  syntax(exp,extension,semantics) = {
    arith   -> spaces a=atom tail^(a);
    arith   -> spaces x=extension tail^(x);
    atom    -> i=int | '(' a=exp ')' { a };
    int     -> n=(['0','9'])+ { semantics.int(n) };
    tail(l) -> spaces o=op r=exp { semantics.binExp(l,o,r) };
    tail(l) -> { l };
    op      -> ('/' | '*')
  };
  semantics = arith.semantics
}

paramBase = {
  syntax(exp,extension,semantics) = {
    start -> '(' e=exp ')' { semantics.inc(e) };
    start -> e=extension { e };
    start -> '0' { semantics.zero }
  };
  semantics = {
    inc(e) = [| [${e}] |];
    zero   = [| 0 |]
  }
}

example63() =
  letrec lang = arithBase.syntax(thunk(lang),{},arithBase.semantics)
  in intern lang {10*20}

example64() =
  letrec lang = paramBase.syntax(thunk(lang),{start->fail},paramBase.semantics)
  in intern lang {((((0))))}
  
notKey(s) =
  case s {
    'bind' -> false;
    'lam' -> false;
    'to' -> false;
    'for' -> false;
     x -> true
  }
  
name = { name -> spaces n=['a','z']+ ?notKey(asString(n)) { asString(n) } }
  
var = {
  var -> n=name { Var(n) }
}

lambda = {
  syntax(operator,exp,semantics) = {
    start -> lam | app | var;
    lam -> spaces 'lam' arg=name dot body=exp { semantics.lambda(arg,body) };
    dot -> spaces '.';
    app -> e=operator '(' a=exp ')' { semantics.app(e,a) }
  };
  semantics = {
    lambda(arg,exp) = Lambda([arg],exp);
    app(o,a) = Apply(o,[a])
  }
}

example65() = 
  letrec 
    lang = arithBase.syntax(thunk(lang),lambda.syntax(thunk(lang.atom),thunk(lang),lambda.semantics),arithBase.semantics)
  in let f = intern lang { lam x . x / 2 }
     in f(100)

example66() = 
  letrec 
    lang = paramBase.syntax(thunk(lang),lambda.syntax(name,thunk(lang),lambda.semantics),paramBase.semantics)
  in let f = intern lang { lam x . ((x)) }
     in f(100)

bind = {
  syntax(value,body,semantics) = {
    start -> local | var;
    local -> bind n=name to v=value for b=body { semantics.bind(n,v,b) };
    bind -> spaces 'bind';
    to -> spaces 'to';
    for -> spaces 'for'
  };
  semantics = {
    bind(name,value,body) = 
      [| let ${name} = ${value} in ${body} |]
  } 
} 

example67() = 
  letrec 
    lang = arithBase.syntax(thunk(lang),bind.syntax(thunk(lang),thunk(lang),bind.semantics),arithBase.semantics)
  in intern lang { bind x to 30 for x * 10 }
  
example68() =
  letrec 
    letLang = bind.syntax(thunk(lang),thunk(lang),bind.semantics)
    op = { start -> var | '(' o=lang ')' { o } }
    funLang = lambda.syntax(op,thunk(lang),lambda.semantics)
    lang = letLang + funLang
    k = 100
  in intern lang { bind f to lam x. bind g to lam y. y for g(x) for f(k) } 
  
example69() =
  letrec 
    letLang = bind.syntax(thunk(funLang),thunk(lang),bind.semantics)
    op = { start -> var | '(' o=funLang ')' { o } }
    funLang=lambda.syntax(op,thunk(funLang),lambda.semantics)
    lang = letLang + funLang
    k = 100
  in intern lang { bind f to lam x. x for bind g to lam y. y for (f(g))(k) }
  
example70() =
  letrec 
    letLang = bind.syntax(thunk(funLang),thunk(lang),bind.semantics)
    op = { start -> var | '(' o=funLang ')' { o } }
    funLang=lambda.syntax(op,thunk(funLang),lambda.semantics)
    lang = letLang + funLang
    k = 100
  in intern lang { bind f to bind g to lam x.x for g for f(k) }
  
example71() =
  letrec 
    letLang = bind.syntax(thunk(funLang),thunk(lang),bind.semantics)
    op = { start -> var | '(' o=funLang ')' { o } }
    funLang=lambda.syntax(op,thunk(funLang),lambda.semantics)
    lang = letLang + funLang
    k = 100
  in intern lang { bind f to lam x.x for f(k) }
  
example72() =
  letrec 
    letLang = bind.syntax(thunk(lang),thunk(lang),bind.semantics)
    op = { start -> var | '(' o=funLang ')' { o } }
    funLang=lambda.syntax(op,thunk(funLang),lambda.semantics)
    lang = letLang + funLang
    k = 100
  in intern lang { bind f to bind g to lam x.x for g for f(k) }
  
complete(semantics) =
  letrec 
    letLang = bind.syntax(thunk(lang),thunk(lang),bind.semantics)
    op = { start -> var | '(' o=lang ')' { o } }
    funLang = lambda.syntax(op,thunk(lang),lambda.semantics)
    lang = arithBase.syntax(thunk(lang),letLang + funLang,semantics)
    k = 100
  in intern lang { bind double to lam x. x * 2 for bind k to 100 for double(k) }
  
example73() = complete(arith.semantics)

example74() = complete(rational.semantics) 

// Examples from section Add Error Language Morphism

addError(arithL) = {    
  syntax = arithL.syntax;    
  semantics = {      
    int(x) = [| fun(k) k(${arithL.semantics.int(x)}) |];      
    binExp(left,op,right) =         
      [| fun(k)             
          ${left}(fun(x)               
           ${right}(fun(y)
            ${if op = '/'
              then [| if y = 0 then 'division by 0' 
                      else k(${arithL.semantics.binExp([|x|],op,[|y|])}) |]
              else k(${arithL.semantics.binExp([|x|],op,[|y|])})})) |] 
  } 
}

example75() =
  let contArith = addError(arith)
  in let f = intern contArith.syntax(contArith.semantics) {100/5}
         k = fun(x) x
     in f(k)

example76() =
  let contArith = addError(arith)
  in let f = intern contArith.syntax(contArith.semantics) {100/0}
         k = fun(x) x
     in f(k)
  
addError2(arithL,isZero) = {    
 syntax = arithL.syntax;    
 semantics = {      
  int(x) = [| fun(k) k(${arithL.semantics.int(x)}) |];      
  binExp(left,op,right) =         
    [| fun(k)             
        ${left}(fun(x)               
         ${right}(fun(y)
          ${if op = '/'
            then [| if ${isZero}(y) then 'division by 0' 
                    else k(${arithL.semantics.binExp([|x|],op,[|y|])}) |]
            else k(${arithL.semantics.binExp([|x|],op,[|y|])})})) |] 
  }  
}

example77() =
  let contArith = addError2(arith,[| fun(n) n = 0 |])
  in let f = intern contArith.syntax(contArith.semantics) {100/5}
         k = fun(x) x
     in f(k)

example78() =
  let contArith = addError2(arith,[| fun(n) n = 0 |])
  in let f = intern contArith.syntax(contArith.semantics) {100/0}
         k = fun(x) x
     in f(k)

example79() =
  let contRational = addError2(rational,[| fun(r) r.den = 0 |])
  in let f = intern contRational.syntax(contRational.semantics) {100/5}
        k = fun(x) x
     in f(k)

example80() =
  let contRational = addError2(rational,[| fun(r) r.num = 0 |])
  in let f = intern contRational.syntax(contRational.semantics) {100/0}
        k = fun(x) x
     in f(k)
     
idMonad = { unit(x) = x; bind(x,f) = f(x) }

arithM = {
  syntax =  arithBase.syntax;
  monad = idMonad;
  semantics(monad) = {
    binExp(left,op,right) = [| 
      import ${monad} { 
        bind(${left},fun(x) 
          bind(${right},fun(y) 
            unit(${BinExp([| x |],op,[| y |])}))) 
      } 
    |];
    int(cs) = [| import ${monad} { unit(${Int(cs)}) } |]
  }
} 

example81() =
  letrec lang = arithM.syntax(thunk(lang),{},arithM.semantics([| arithM.monad |]))
  in intern lang {10*20}
  
rationalM = {
  syntax = arithM.syntax;
  monad = idMonad;
  semantics(monad) = {
    binExp(left,op,right) = 
      case op {
        '*' ->
         [| import ${monad} { 
              bind(${left},fun(x) 
                bind(${right},fun(y) 
                  unit({ num=x.num * y.num; den=x.den * y.den })))   
            }  |];
        '/' ->
         [| import ${monad} { 
              bind(${left},fun(x) 
                bind(${right},fun(y) 
                  ${rationalM.semantics(monad).binExp([| unit(x) |],'*',
                     [| unit({ num=y.den;den=y.num }) |])})) 
            } |]
       };
    int(cs) = [| import ${monad} { unit({ num=${Int(cs)}; den=1 }) } |]
  }
}

example82() =
  letrec lang = rationalM.syntax(thunk(lang),{},rationalM.semantics([| rationalM.monad |]))
  in intern lang {10/20}
  
// Examples from section The List Monad
  
ND(L) = {
  syntax(exp,extension,semantics) = 
    L.syntax(
      exp,
      extension,
      semantics);
  monad = {
    bind(x,f) = flatten(map(fun(v) L.monad.bind(v,f),x));
    unit(x) = [L.monad.unit(x)]
  };
  semantics(monad) = L.semantics(monad)
}  

example83() =
  letrec 
    syntax = (ND(arithM)).syntax
    semantics = (ND(arithM)).semantics
    monad = [| (ND(arithM)).monad |]
    lang = syntax(thunk(lang),{start->fail},semantics(monad))
  in intern lang {100*2*3}
  
  
ND2(L) = {
  syntax(exp,extension,semantics) = 
    L.syntax(
      exp,
      { start -> 
          '<' x=exp ',' y=exp '>' 
          { semantics.values(x,y) } 
      } + extension,
      semantics);
  monad = {
    bind(x,f) = flatten(map(fun(v) L.monad.bind(v,f),x));
    unit(x) = [L.monad.unit(x)]
  };
  semantics(monad) = 
    L.semantics(monad) + 
    { values(x,y) = [| append(${x},${y}) |] }
}  
  
example84() =
  letrec 
    syntax = (ND2(arithM)).syntax
    semantics = (ND2(arithM)).semantics
    monad = [| (ND2(arithM)).monad |]
    lang = syntax(thunk(lang),{xxx -> fail},semantics(monad))
  in intern lang {<100,400>*2*<3,4>}
  
example85() =
  letrec 
    syntax = (ND2(arithM)).syntax
    semantics = (ND2(arithM)).semantics
    monad = [| (ND2(arithM)).monad |]
    lang = syntax(thunk(lang),{xxx -> fail},semantics(monad))
  in intern lang {<100,400>*2*<3,4>}
  
example86() =
  letrec 
    syntax = (ND2(rationalM)).syntax
    semantics = (ND2(rationalM)).semantics
    monad = [| (ND2(rationalM)).monad |]
    lang = syntax(thunk(lang),{xxx -> fail},semantics(monad))
  in intern lang {(<100,400>/<2,4>)/<0,4>}
  
example87() =
  letrec 
    syntax = (ND2(ND2(arithM))).syntax
    semantics = (ND2(ND2(arithM))).semantics
    monad = [| (ND2(ND2(arithM))).monad |]
    lang = syntax(thunk(lang),{xxx -> fail},semantics(monad))
  in intern lang {<100,400>*2*<3,4>}
  
NDArith(L) = {
  syntax = (ND2(L)).syntax;
  monad = (ND2(L)).monad;
  semantics(monad) = 
   let binExp = (ND2(L)).semantics(monad).binExp
   in { binExp(l,op,r) =
          if op='/'
          then binExp(print(l),op,[| remove(0,${r}) |])
          else binExp(l,op,r) } + 
       (ND2(L)).semantics(monad)
}

example88() =
  letrec 
    syntax = (NDArith(arithM)).syntax
    semantics = (NDArith(arithM)).semantics
    monad = [| (NDArith(arithM)).monad |]
    lang = syntax(thunk(lang),{xxx -> fail},semantics(monad))
  in intern lang {(<100,400>/<2,4>)/<0,4>}
  
  
example89() =
  let x = [| a > b |]
  in [| if ${x} then y else z |]  
  
example90() =
  let g = { start -> 'add(' x=start ')' { [| ${x} + 1 |] } 
                  |  '0' { [| 0 |] } 
                  |  '<' x=XPL '>' { 'exp.Drop'(x) }
          }
      x = [| 2 + 3 |]
  in [ g | add(add(<x>)) |]
  
  
  