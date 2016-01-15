export test1,test2,test3,test3a,test4,test5,test6,test6a,test7

import 'src/xpl/exp.xpl'
import 'src/xpl/lists.xpl'

arith = {
  syntax(exp,extension,semantics) = {
    arith      -> whitespace (a=atom tail^(a) | x=extension tail^(x));
    atom       -> i=int | '(' a=exp ')' { a } ;
    whitespace -> (32 | 10 | 9 | 13)*;  
    int        -> n=(['0','9'])+ { semantics.int(n) };
    tail(left) -> o=op right=exp { semantics.binExp(left,o,right) } | { left };
    op         -> whitespace ('/' | '*')
  };
  monad = {
    bind(x,f) = f(x);
    unit(x) = x
  };
  semantics(monad) = {
    binExp(left,op,right) = [| import ${monad} { bind(${left},fun(x) bind(${right},fun(y) unit(${BinExp([| x |],op,[| y |])}))) } |];
    int(cs)               = [| import ${monad} { unit(${Int(cs)}) } |]
  }
} 

rational = {
  syntax = arith.syntax;
  monad = {
    bind(x,f) = f(x);
    unit(x) = x
  };
  semantics(monad) = {
    binExp(left,op,right) = 
      case op {
        '*' -> [| import ${monad} { bind(${left},fun(x) bind(${right},fun(y) unit({ num=x.num * y.num; den=x.den * y.den }))) } |];
        '/' -> [| import ${monad} { bind(${left},fun(x) bind(${right},fun(y) ${rational.semantics.binExp([|unit(x)|],'*',[| unit({ num=y.den;den=y.num }) |])})) } |]       
      };
    int(n) = [| import ${monad} { unit({ num=${Int(n)};den=1 }) } |]
  }
}

lambda = {
  syntax(exp,whitespace,semantics) = {
    start -> 'lam' whitespace arg=name whitespace '.' whitespace body=exp { semantics.lambda(arg,body) };
    name   -> ['a','z']+
  };
  semantics(monad) = {
    lambda(arg,exp) = [| import ${monad} { unit(${Lambda([arg],exp) }) } |]
  }
}

ND(L) = {
  syntax(exp,extension,semantics) = L.syntax(exp,{ start -> '<' x=exp ',' y=exp '>' { semantics.values(x,y) } }+extension,semantics);
  monad = {
    bind(x,f) = flatten(map(fun(v) L.monad.bind(v,f),x));
    unit(x) = [L.monad.unit(x)]
  };
  semantics(monad) = L.semantics(monad) + { values(x,y) = [| append(${x},${y}) |] }
}

NDArithOld(L) = {
  syntax(exp,extension,semantics) = L.syntax(exp,{ start -> '<' x=exp ',' y=exp '>' { semantics.values(x,y) } }+extension,semantics);
  monad = {
    bind(x,f) = flatten(map(fun(v) L.monad.bind(v,f),x));
    unit(x) = [L.monad.unit(x)]
  };
  semantics(monad) = 
    { binExp(l,op,r) =
        if op='/'
        then (L.semantics(monad)).binExp(l,op,[| removeIf(${r},fun(x) x = 0) |])
        else (L.semantics(monad)).binExp(l,op,r) } +
    L.semantics(monad) + { values(x,y) = [| append(${x},${y}) |] }
}

NDArith(L) = {
  syntax = (ND(L)).syntax;
  monad = (ND(L)).monad;
  semantics(monad) = 
    { binExp(l,op,r) =
        if op='/'
        then (L.semantics(monad)).binExp(l,op,[| removeIf(${r},fun(x) x = 0) |])
        else (L.semantics(monad)).binExp(l,op,r) } + (ND(L)).semantics(monad)
}

test1() = 
  letrec lang = arith.syntax(thunk(lang),{dummy->'dummy'},arith.semantics([| arith.monad |]))
  in intern lang { 100 * 2 }

test2() = 
  letrec lang = (ND(arith)).syntax(thunk(lang),{dummy->'dummy'},(ND(arith)).semantics([| (ND(arith)).monad |]))
  in intern lang { 100 * 2 * 3 }

test3() = 
  letrec lang = (ND(arith)).syntax(thunk(lang),{dummy->'dummy'},(ND(arith)).semantics([| (ND(arith)).monad |]))
  in intern lang { <100,400> * 2 * <3,4> }

test3a() = 
  letrec lang = (ND(ND(arith))).syntax(thunk(lang),{dummy->'dummy'},(ND(ND(arith))).semantics([| (ND(ND(arith))).monad |]))
  in intern lang { <100,400> * 2 * <3,4> }

test4() = 
  letrec lang = (ND(arith)).syntax(thunk(lang),lambda.syntax(thunk(lang),thunk(lang.whitespace),lambda.semantics([| (ND(arith)).monad |])),(ND(arith)).semantics([| (ND(arith)).monad |]))
  in intern lang { <lam x. <100,400> * 2 * <3,4>,90> }

test5() = 
  letrec lang = rational.syntax(thunk(lang),{dummy->'dummy'},rational.semantics([| rational.monad |]))
  in intern lang { 100 * 2 }

test6() = 
  letrec lang = (ND(rational)).syntax(thunk(lang),{dummy->'dummy'},(ND(rational)).semantics([| (ND(rational)).monad |]))
  in intern lang { <100,400> * 2 * <3,4> }
  
test6a() = 
  letrec lang = (ND(rational)).syntax(thunk(lang),{dummy->'dummy'},(ND(rational)).semantics([| (ND(rational)).monad |]))
  in intern lang { <100,400> * 2 * <0,4> }

test7() = 
  letrec lang = (NDArith(arith)).syntax(thunk(lang),{dummy->'dummy'},(NDArith(arith)).semantics([| (NDArith(arith)).monad |]))
  in intern lang { (<100,400> / 2) / <0,4>  }