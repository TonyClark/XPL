export gcd, gcl

import 'src/xpl/exp.xpl'
import 'src/xpl/xpl.xpl'

// A guarded command language:
//
// command ::= 
//   v := exp
// | abort
// | if (exp -> command)* fi
// | do (exp -> command)* od
// | begin command* end 
// exp ::= int | var | extern | exp op exp
//
// Note that extern is in the surrounding scope.

// Define the semantics of the language...

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
  
// Here is the grammar..

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

// Here is an application of the grammar...

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