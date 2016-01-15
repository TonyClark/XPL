export 
  test1,test2,test2a,test3,test3a,test4,test4a,test5,test6,test7,test8,test9,test10,
  test11,test12,test13,test14,test15,test16,test17,test18,test19,test20

import 'src/xpl/exp.xpl'
import 'src/xpl/lists.xpl'

arithExec = {
  syntax(semantics) = {
    arith      -> whitespace a=atom tail^(a);
    atom       -> i=int | '(' a=arith ')' { a };
    whitespace -> (32 | 10 | 9 | 13)*;  
    int        -> n=(['0','9'])+ { semantics.int(n) };
    tail(left) -> o=op right=arith { semantics.binExp(left,o,right) } | { left };
    op         -> whitespace ('/' | '*')
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

postFix = {
  syntax(semantics) = {
    postfix    -> a=atom tail^(a);
    tail(left) -> right=atom o=op n={ semantics.binExp(left,o,right) } tail^(n);
    tail(left) -> { left };
    atom       -> int | '(' a=postfix ')' { a };
    whitespace -> (32 | 10 | 9 | 13)*;  
    int        -> whitespace n=(['0','9'])+ { semantics.int(n) };
    op         -> whitespace ('/' | '*')
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

arithCalc(arithL) = {
  syntax = arithL.syntax;
  semantics = {
    binExp(left,op,right) = {
      calc = op;
      children = [left,right];
      value = arithL.semantics.binExp(left.value,op,right.value)
    };
    int(cs) = {
      calc = 'int'; 
      children = []; 
      value = arithL.semantics.int(cs) 
    }
  }
}

arith = {
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

arithP(op) = {
  syntax(semantics) = {
    arith      -> whitespace a=atom tail^(a);
    atom       -> i=int | '(' a=arith ')' { a };
    whitespace -> (32 | 10 | 9 | 13)*;  
    int        -> n=(['0','9'])+ { semantics.int(n) };
    tail(left) -> whitespace o=op right=arith { semantics.binExp(left,o,right) } | { left }
  };
  semantics = {
    binExp(left,op,right) = BinExp(left,op,right);
    int(cs)               = Int(cs)
  }
}

arithPara = {
  syntax(exp,extension,semantics) = {
    arith      -> whitespace a=atom tail^(a);
    arith      -> whitespace x=extension tail^(x);
    atom       -> i=int | '(' a=exp ')' { a } ;
    whitespace -> (32 | 10 | 9 | 13)*;  
    int        -> n=(['0','9'])+ { semantics.int(n) };
    tail(left) -> o=op right=exp { semantics.binExp(left,o,right) } | { left };
    op         -> whitespace ('/' | '*')
  };
  semantics = {
    binExp(left,op,right) = BinExp(left,op,right);
    int(cs)               = Int(cs)
  }
}

addError(arith,isZero) = {
   syntax = arith.syntax;
   semantics = {
     int(x) = [| fun(k) k(${arith.semantics.int(x)}) |];
     binExp(left,op,right) = 
       [| fun(k)
            ${left}(fun(x)
              ${right}(fun(y)
                ${if op = '/'
                  then [| if ${isZero}(y)
                          then 'division by 0'
                          else k(${arith.semantics.binExp([|x|],op,[|y|])}) |]
                  else [| k(${arith.semantics.binExp([|x|],op,[|y|])}) |]})) |]
   }
 }
       

var = {
  syntax(semantics) = {
    var -> n=name { semantics.var(n) };
    name   -> not('let') not('lam') not('in') ['a','z']+
  };
  semantics = {
    var(n) = Var(n)
  }
}

lambda = {
  syntax(operator,exp,whitespace,var,name,semantics) = {
    start -> lam | app | var;
    lam -> 'lam' whitespace arg=name whitespace '.' whitespace body=exp { semantics.lambda(arg,body) };
    app -> e=operator '(' a=exp ')' { semantics.app(e,a) }
  };
  semantics = {
    lambda(arg,exp) = Lambda([arg],exp);
    app(o,a) = Apply(o,[a])
  }
}

bind = {
  syntax(value,body,whitespace,var,name,semantics) = {
    start -> bind | var;
    bind -> 'let' whitespace n=name whitespace '=' whitespace v=value whitespace 'in' whitespace b=body { semantics.bind(n,v,b) }
  };
  semantics = {
    bind(name,value,body) = [| let ${name} = ${value} in ${body} |]
  } 
}   

rationalExec = {
  syntax = arith.syntax;
  semantics = {
    binExp(left,op,right) = 
      case op {
        '*' -> { num=left.num * right.num; den=left.den * right.den };
        '/' -> rationalExec.semantics.binExp(left,'*',{ num=right.den;den=right.num })
      };
    int(n) = { num=asInt(n);den=1 }
  }
}

rational = {
  syntax = arith.syntax;
  semantics = {
    binExp(left,op,right) = 
      case op {
        '*' -> [| { num=${left}.num * ${right}.num; den=${left}.den * ${right}.den } |];
        '/' -> rational.semantics.binExp(left,'*',[| { num=${right}.den;den=${right}.num } |])
      };
    int(n) = [| { num=${Int(n)};den=1 } |]
  }
}

test1() = 
  let lang = arith.syntax(arith.semantics)
  in intern lang {
       (10) * 20
     }


test2() = 
  let lang = arith.syntax(arith.semantics)
  in intern lang {
       (40) / 20
     }

test2a() = 
  let lang = arith.syntax((addError(arith,[|fun(x) x = 0|])).semantics)
  in intern lang {
       (40) / 0
     }

test3() = 
  let lang = rational.syntax(rational.semantics)
  in intern lang {
       (1/2) * (1/2)
     }

test3a() = 
  let lang = arith.syntax(arith.semantics)
  in intern lang {
       (1/2) * (1/2)
     }

test4() = 
  let lang = rational.syntax(rational.semantics)
  in intern lang {
       (1/2) / (1/2) / 1
     }

test4a() = 
  let lang = rational.syntax((addError(rational,[| fun(r) r.num = 0 |])).semantics)
  in intern lang {
       (1/2) / 0
     }

test5() = 
  letrec lang = arithPara.syntax(thunk(lang),{x->'x'},arithPara.semantics).setDebug(true)
  in intern lang {
       2 * 10
     }
     
test6() = 
  letrec 
    id = var.syntax(var.semantics)
    name = (var.syntax(var.semantics)).name
    lang = arithPara.syntax(thunk(lang),lambda.syntax(thunk(lang.atom),thunk(lang),thunk(lang.whitespace),id,name,lambda.semantics),arithPara.semantics)
  in intern lang { lam x . 10 / 2 }
     
test7() = 
  letrec 
    id = var.syntax(var.semantics)
    name = (var.syntax(var.semantics)).name
    lang = arithPara.syntax(thunk(lang),lambda.syntax(thunk(lang.atom),thunk(lang),thunk(lang.whitespace),id,name,lambda.semantics),rational.semantics)
  in intern lang { (lam x . x * 2)(10) }
     
test8() = 
  letrec
    id = var.syntax(var.semantics)
    name = (var.syntax(var.semantics)).name
    lang = arithPara.syntax(thunk(lang),bind.syntax(thunk(lang),thunk(lang),thunk(lang.whitespace),id,name,bind.semantics) + lambda.syntax(thunk(lang.atom),thunk(lang),thunk(lang.whitespace),id,name,lambda.semantics),rational.semantics)
  in intern lang { let f = lam x . 10  in (f)(100) }
  
test9() =
  letrec
    id = var.syntax(var.semantics)
    name = (var.syntax(var.semantics)).name
    dLang = thunk(lang)
    operator = { op -> id | '(' o=dLang ')' { o } }
    lang = lambda.syntax(operator,thunk(lang),{ whitespace -> (32 | 10 | 9 | 13)* },id,name,lambda.semantics)
  in intern lang { lam f. lam x . (lam x. x)(x) }
  
test10() =
  letrec
    id = var.syntax(var.semantics)
    name = (var.syntax(var.semantics)).name
    lang = bind.syntax(thunk(lang),thunk(lang),{ whitespace -> (32 | 10 | 9 | 13)* },id,name,bind.semantics)
  in intern lang { let x = let y = head in y in x }
  
test11() =
  letrec
    id = var.syntax(var.semantics)
    name = (var.syntax(var.semantics)).name
    dLang = thunk(lang)
    letLang = bind.syntax(dLang,dLang,{ whitespace -> (32 | 10 | 9 | 13)* },id,name,bind.semantics)
    operator = { op -> id | '(' o=dLang ')' { o } }
    funLang = lambda.syntax(operator,dLang,{ whitespace -> (32 | 10 | 9 | 13)* },id,name,lambda.semantics)
    lang = letLang + funLang
  in intern lang { let f = lam x . let g = lam y . y in g(x) in f(head) }
  
test12() =
  letrec
    id = var.syntax(var.semantics)
    name = (var.syntax(var.semantics)).name
    dLang = thunk(lang)
    dFunLang = thunk(funLang)
    operator = { op -> id | '(' o=funLang ')' { o } }
    funLang = lambda.syntax(operator,dFunLang,{ whitespace -> (32 | 10 | 9 | 13)* },id,name,lambda.semantics)
    letLang = bind.syntax(funLang,dLang,{ whitespace -> (32 | 10 | 9 | 13)* },id,name,bind.semantics)
    lang = letLang + funLang
  in intern lang { let f = lam x . x in let g = lam y . y in (f(g))(head) }
  
complete1(semantics) =
  letrec
    id = var.syntax(var.semantics)
    name = (var.syntax(var.semantics)).name
    dLang = thunk(lang)
    letLang = bind.syntax(dLang,dLang,{ whitespace -> (32 | 10 | 9 | 13)* },id,name,bind.semantics)
    operator = { op -> id | '(' o=dLang ')' { o } }
    funLang = lambda.syntax(operator,dLang,{ whitespace -> (32 | 10 | 9 | 13)* },id,name,lambda.semantics)
    lang = arithPara.syntax(dLang,letLang + funLang,semantics)
  in intern lang { let f = lam x . x * 2 in let k = 100 in f(k) }
  
test13() = complete1((addError(arithPara)).semantics)
  
test14() = complete1(rational.semantics)

test15() = (arithExec.syntax(arithExec.semantics)).parse('10 * 20',[])

test16() = 
  let lang = arithCalc(arithExec)
  in (lang.syntax(lang.semantics)).parse('10 * 20',[])
  
test17() =
  intern (postFix.syntax(postFix.semantics)) {
    100 200 *
  }
  
test18() =
 (postFix.syntax(postFix.semantics)).parse('100 200 *',[])
 
test19() =
  let infixSyntax = arith.syntax
      postfixSyntax = postFix.syntax
      intSemantics = arithExec.semantics
      rationalSemantics = rationalExec.semantics
      intCalcSemantics = (arithCalc(arithExec)).semantics
      rationalCalcSemantics = (arithCalc(rationalExec)).semantics
  in let infixIntExecL = {syntax=infixSyntax;semantics=intSemantics}
         postfixIntExecL = {syntax=postfixSyntax;semantics=intSemantics}
         infixRationalExecL = {syntax=infixSyntax;semantics=rationalSemantics}
         postfixRationalExecL = {syntax=postfixSyntax;semantics=rationalSemantics}
         infixIntCalcL = {syntax=infixSyntax;semantics=intCalcSemantics}
         postfixIntCalcL = {syntax=postfixSyntax;semantics=intCalcSemantics}
         infixRationalCalcL = {syntax=infixSyntax;semantics=rationalCalcSemantics}
         postfixRationalCalcL = {syntax=postfixSyntax;semantics=rationalCalcSemantics}
         test(L,s) = (L.syntax(L.semantics)).parse(s,[])
     in [test(postfixRationalCalcL,'10 2 /'),
         test(infixIntExecL,'10 / 2'),
         test(postfixIntExecL,'10 2 /'),
         test(infixRationalExecL,'10 / 2'),
         test(postfixRationalExecL,'10 2 /'),
         test(infixIntCalcL,'10 / 2'),
         test(postfixIntCalcL,'10 2 /'),
         test(infixRationalCalcL,'10 / 2'),
         test(postfixRationalCalcL,'10 2 /'),
         let L = arithCalc(infixIntCalcL) in test(L,'10 / 2')]

test20() =
  let m1 = arithP({op -> '+' | '-'})
      m2 = arithP({op -> '*' | '/'})
      m3 = arithP({op -> '+' | '-'}+{op -> '*' | '/'})
  in let l1 = m1.syntax(m1.semantics)
         l2 = m2.syntax(m2.semantics)
         l3 = m3.syntax(m3.semantics)
     in intern l3 { 10 + 20 * 30 }