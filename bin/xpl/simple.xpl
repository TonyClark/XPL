export grammar1,grammar2,grammar3,grammar4,exp,test1,test2

import 'src/xpl/exp.xpl'

grammar1 = { start -> 'x' { [| 1 |] } }
grammar2 = { start -> 'y' { [| 2 |] } }
grammar3 = {
  start -> first^([1,2,3]);
  first([]) -> { [| [] |] };
  first([]) -> { [| [100] |] };
  first(x:l) -> e=first^(l) { [| (${x.lift()} + 1):${e} |] }
}
grammar4 = {
  start -> rule^(BinExp([| 1 |],'*',[| 2 |]));
  rule(BinExp(x,'*',y)) -> { [| ${x} + ${y} |] }
}

isTighter(o,oo) =
  case o,oo {
    '+','*' -> true;
    o,oo -> false
  }
  
isLeft(o) =
  case o {
    '*' -> false;
    '+' -> false;
    '>' -> true
  }
  
isRight(o) = 
  case isLeft(o) {
    true -> false;
    false -> true
  }
  
equals(x,y) = x = y

prec(o) =
  case o {
    '>' -> 10;
    '*' -> 9;
    '+' -> 8
  }
  
legal(exp,p) = 
  case exp {
    BinExp(l,o,BinExp(ll,oo,rr)) when prec(o) = prec(oo) ->
      isLeft(o) = false and
      (prec(o) < p or prec(o) = p) and
      legal(l,prec(o)) and
      legal(BinExp(ll,oo,rr),prec(oo));
    BinExp(BinExp(l,o,r),oo,rr) when prec(o) = prec(oo) ->
      isLeft(o) and
      (prec(o) < p or prec(o) = p) and
      legal(BinExp(l,o,r),prec(o)) and
      legal(rr,prec(oo));
    BinExp(l,o,r) -> 
      if prec(o) < p or prec(o) = p
      then legal(l,prec(o)) and legal(r,prec(o))
      else false;
    x -> true
  }
  
exp = {
  exp -> a=atom postAtom^(a);
  postAtom(l) -> o=op r=atom e=insert^(l,o,r) postAtom^(e);
  postAtom(e) -> { e };
  insert(BinExp(l,o,r),oo,rr) -> ?isTighter(print(oo),o) x=insert^(r,oo,rr) { BinExp(l,o,x) };
  insert(BinExp(l,o,r),oo,rr) -> ?isTighter(print(o),oo) { BinExp(BinExp(l,o,r),oo,rr) };
  insert(BinExp(l,o,r),oo,rr) -> ?isRight(o) { BinExp(l,o,BinExp(r,oo,rr)) };
  insert(BinExp(l,o,r),oo,rr) -> ?isLeft(print(o)) { BinExp(BinExp(l,o,r),oo,rr) };
  insert(l,o,r) -> { BinExp(l,o,r) };
  atom -> var | int;
  var -> n=name { Var(asString(n)) };
  int -> whitespace n=numeric+ { Int(asInt(n)) };
  op -> '*' | '+' | '>';
  whitespace -> (32 | 10 | 9 | 13)*;  
  name   -> whitespace not('machine') not('on') l=alpha ls=alphaNumeric* { asString(l:ls) };
  alpha  -> ['a','z'];
  alpha  -> ['A','Z'];
  numeric -> ['0','9'];
  alphaNumeric -> alpha | numeric
}
  
exp2 = {
  root -> e=exp ';' ?legal(e,10) {e};
  exp -> a=atom postAtom^(a);
  postAtom(l) -> o=op r=exp postAtom^(BinExp(l,o,r));
  postAtom(e) -> { e };
  atom ->whitespace l=alpha ls=alphaNumeric* { Var(asString(l:ls)) };
  op -> '*' | '+' | '>';
  whitespace -> (32 | 10 | 9 | 13)*;  
  alpha  -> ['a','z'];
  alpha  -> ['A','Z'];
  numeric -> ['0','9'];
  alphaNumeric -> alpha | numeric
}

greater(x,y) = x > y
greatereql(x,y) = (x > y) or (x = y)
less(x,y) = x < y
equals(x,y) = x=y

insert(l,o,r) =
  case l {
    BinExp(ll,oo,rr) when prec(o) < prec(oo) -> BinExp(ll,oo,insert(rr,o,r));
    BinExp(ll,oo,rr) when prec(o) > prec(oo) -> BinExp(l,o,r);
    BinExp(ll,oo,rr) when (prec(o) = prec(oo)) and (isLeft(o)) -> BinExp(l,o,r);
    BinExp(ll,oo,rr) when (prec(o) = prec(oo)) and isRight(oo) -> BinExp(ll,oo,insert(rr,o,r));
    x -> BinExp(x,o,r)
  }
  
atomic(e) = 
  case e {
    BinExp(l,o,r) -> false;
    x -> true
  }
  
exp3 = {
  root -> e=exp^(0) ';'  {e};
  exp(p) -> a=atom e=postAtom^(a,p) {e};
  postAtom(BinExp(l,o,r),p) -> oo=op ?equals(prec(o),p) ?equals(prec(oo),p) ?isLeft(oo) rr=atom x={BinExp(BinExp(l,o,r),oo,rr)} e=postAtom^(x,prec(o))  {e};
  postAtom(BinExp(l,o,r),p) -> oo=op ?equals(prec(o),p) ?equals(prec(oo),p) ?isRight(oo) rr=atom x={insert(BinExp(l,o,r),oo,rr)} e=postAtom^(x,prec(o))  {e};
  postAtom(l,p) -> ?atomic(l) o=op ?equals(prec(o),p) r=atom x={BinExp(l,o,r)} e=postAtom^(x,prec(o)){e};
  postAtom(l,p) -> o=op ?greater(prec(o),p) r=atom x={BinExp(l,o,r)} e=postAtom^(x,prec(o))  {e};
  postAtom(l,p) -> o=op ?less(prec(o),p) r=atom x={insert(l,o,r)} e=postAtom^(x,p) {e};
  postAtom(e,p) -> { e };
  atom ->whitespace l=alpha ls=alphaNumeric* { Var(asString(l:ls)) };
  op -> '*' | '+' | '>';
  whitespace -> (32 | 10 | 9 | 13)*;  
  alpha  -> ['a','z'];
  alpha  -> ['A','Z'];
  numeric -> ['0','9'];
  alphaNumeric -> alpha | numeric
}

test1() = 
  exp2.parse('a + b + c * g + h + i > d + e + f * k + l + m;',[])

test2() = 
  exp3.parse('c + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h + d + e * f + g + h ;',[])
  