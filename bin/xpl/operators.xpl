export test1

import 'src/xpl/exp.xpl'
  

  
exp = {
  syntax(semantics) = 
    import semantics { {
      root        -> e=exp ';' ?legal(e,10,semantics) {e};
      exp         -> a=atom postAtom^(a);
      postAtom(l) -> o=operator r=exp postAtom^(binExp(l,o,r));
      postAtom(e) -> { e };
      atom        -> whitespace c=['a','z']  { var(asString([c])) };
      operator    -> whitespace o=. op={asString([o])} ?isOp(op) {op};
      whitespace  -> (32 | 10 | 9 | 13)*
    } 
  };
  semantics = {
    binExp(l,o,r) = BinExp(l,o,r);
    var(n) = Var(n);
    isOp(o) = 
      case o {
        '+' -> true;
        '*' -> true;
        '>' -> true;
        o -> false
      };
    isLeft(o) =
      case o {
        '*' -> true;
        '+' -> false;
        '>' -> false
     };
    prec(o) =
      case o {
        '>' -> 10;
        '*' -> 8;
        '+' -> 9
      };
    legal(exp,p,semantics) = 
      import semantics {
        case exp {
          BinExp(l,o,BinExp(ll,oo,rr)) when prec(o) = prec(oo) ->
            isLeft(o) = false and
            (prec(o) < p or prec(o) = p) and
            legal(l,prec(o),semantics) and
            legal(BinExp(ll,oo,rr),prec(oo),semantics);
          BinExp(BinExp(l,o,r),oo,rr) when prec(o) = prec(oo) ->
            isLeft(o) and
            (prec(o) < p or prec(o) = p) and
            legal(BinExp(l,o,r),prec(o),semantics) and
            legal(rr,prec(oo),semantics);
          BinExp(l,o,r) -> 
            if prec(o) < p or prec(o) = p
            then legal(l,prec(o),semantics) and legal(r,prec(o),semantics)
            else false;
          x -> true
        }
      }
    }
  }

test1() = 
  (exp.syntax(exp.semantics)).parse('a + b * c + d;',[])
  