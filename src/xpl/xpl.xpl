export xplBase, XPL, test

xplBase(exp,extension) = { 
  root            -> x=letExp ! {x} | x=letrecExp ! {x} | x=ifExp ! {x} | x=internExp ! {x} | x=funExp ! {x} | x=bool ! {x} | x=comment ! {x};
  file(name)      -> ds=(m=moduleDef ! {m})* EOF {'modules.ModuleBinding'(name,ds)};
  whitespace      -> (32 | 10 | 13 | 9)* !;
  comment         -> '//' (not(10) .)*;
  topLevelCommand -> whitespace d=command ';' {d};
  command         -> x=toplvlQuit ! {x} | x=tplvlExp ! {x} | x=tplvlImport ! {x} | x=tplvlBind ! {x};
  tplvlBind       -> n=name whitespace ':=' e=exp {'commands.Bind'(n,e)} |
                     n=name whitespace '(' as=argNames ')' whitespace ':=' e=exp {'commands.Bind'(n,'exp.Lambda'(as,e))}; 
  toplvlQuit      -> 'quit' {'commands.Quit'()};
  tplvlExp        -> e=exp {'commands.Exp'(e)};
  tplvlImport     -> 'import' ns=args {'commands.Import'(ns)};
  letExp          -> 'let' bs=(binding)* 'in' b=exp {'exp.Let'(bs,b)};
  letrecExp       -> 'letrec' bs=(binding)* 'in' b=exp {'exp.Letrec'(bs,b)};
  binding         -> comment binding | moduleBinding | valBinding | funBinding;
  valBinding      -> n=bindingName '=' v=exp {'exp.ValueBinding'(n,v)};
  funBinding      -> n=bindingName '(' as=argNames ')' '=' v=exp {'exp.ValueBinding'(n,'exp.Lambda'(as,v))};
  ifExp           -> 'if' t=exp 'then' c=exp 'else' a=exp {'exp.If'(t,c,a)};
  oldrecord          -> '{' fs=fields '}' {'exp.Record'(fs)};
  
  record          -> '{' recordTail^('exp.Record'([]));
  recordTail(r)   -> '}' { r } | recordName^(r) | recordFun^(r) | recordDrop^(r);
  recordName(r)   -> n=name '=' e=exp recordAfter^('exp.BinExp'(r,'+','exp.Record'(['exp.Field'(n,e)])));
  recordFun(r)    -> n=name '(' as=argNames ')' '=' e=exp recordAfter^('exp.BinExp'(r,'+','exp.Record'(['exp.Field'(n,'exp.Lambda'(as,e))])));
  recordDrop2(r)   -> d=drop '=' e=exp recordAfter^('exp.BinExp'(r,'+','exp.Drop'('exp.Apply'('exp.Str'('exp.Record'),['exp.Apply'('exp.Str'('exp.List'),[['exp.Apply'('exp.Str'('exp.Field'),[d,e.lift()])]])]))));
  recordDrop(r)  -> '${' d=exp '}' '=' e=exp recordAfter^('exp.BinExp'(r,'+','exp.DroppedSingletonRecord'(d,e)));
  recordAfter(r)  -> ';' recordTail^(r) | '}' { r };
  
  fields          -> someFields | {[]};
  someFields      -> f=field fs=(';' field)* {f:fs};
  field           -> funField | valueField;
  valueField      -> n=name '=' e=exp {'exp.Field'(n,e)};
  funField        -> n=name '(' as=argNames ')' '=' e=exp {'exp.Field'(n,'exp.Lambda'(as,e))};
  internExp       -> 'intern' g=exp PDELAY('{','}',g);
  bindingName     -> n=name {'exp.BindingNameLiteral'(n)} | d=drop {'exp.BindingNameDrop'(d)};
  funExp          -> 'fun' '(' as=argNames ')' b=exp {'exp.Lambda'(as,b)};
  bool            -> l=compare (o=boolOp r=bool {'exp.BinExp'(l,o,r)} | {l});
  compare         -> l=arith (o=compareOp r=compare {'exp.BinExp'(l,o,r)} | {l});
  arith           -> l=apply (o=arithOp r=arith {'exp.BinExp'(l,o,r)} | {l});
  boolOp          -> 'and' | 'or';
  compareOp       -> '<' | '>' | '=' | '!=' | ':';
  arithOp         -> '+' | '-' | '*' | '/';
  apply           -> valueApply | send;
  valueApply      -> a=atom '(' as=args ')' {'exp.Apply'(a,as)};
  send            -> t=atom sendTail^(t);
  sendTail(a)     -> t=sendMessage^(a) sendTail^(t) | {a};
  sendMessage(t)  -> '.' (n=name sendOrDot^(t,n) | '${' n=exp '}' sendOrDotDrop^(t,n));
  sendOrDot(t,n)  -> '(' as=args ')' {'exp.Send'(t,n,as)} | {'exp.Dot'(t,n)};
  sendOrDotDrop(t,n)  -> '(' as=args ')' {'exp.Drop'('exp.Apply'('exp.Str'('exp.Send'),[t.lift(),n,as.lift()]))} | {'exp.Drop'('exp.Apply'('exp.Str'('exp.Dot'),[t.lift(),n]))};
  args            -> someArgs | {[]};
  someArgs        -> a=exp as=(',' exp)* {a:as};
  argNames        -> someArgNames | {[]};
  someArgNames    -> a=name as=(',' name)* {a:as};
  atom            -> x=<extension.atom> ! {x} | x=thunkExp ! {x} | x=caseExp ! {x} | x=importExp ! {x} | x=trueExp ! {x} | x=record ! {x} | x=grammar ! {x} | x=falseExp ! {x} | x=lift ! {x} | x=drop ! {x} | x=list ! {x} | x=var ! {x} | x=intExp ! {x} | x=strExp ! {x} | x=parenExp ! {x};
  thunkExp        -> whitespace 'thunk' '(' e=exp ')' {'exp.Thunk'(e)};
  lift            -> '[|' e=exp '|]' {e.lift()} | '[' g=exp  LDELAY('|','|]',g);
  drop            -> '${' d=exp '}' {'exp.Drop'(d)};
  importExp       -> whitespace 'import' es=args whitespace '{' e=exp whitespace '}' {'exp.Import'(es,e)};
  moduleBinding   -> whitespace 'module' n=bindingName whitespace '{' ds=(moduleDef)* whitespace '}' {'modules.moduleBinding'(n,ds)};
  moduleDef       -> comment moduleDef | moduleImport | moduleExport | moduleBind;
  moduleImport    -> whitespace 'import' es=args {'modules.Import'(es)};
  moduleExport    -> whitespace 'export' ns=argNames {'modules.Export'(ns)};
  moduleBind      -> b=binding {'modules.Bind'(b)};
  list            -> '[' es=args ']' {'exp.List'(es)};
  parenExp        -> '(' e=exp ')' {e};
  var             -> n=name {'exp.Var'(n)};
  trueExp         -> 'true' {'exp.True'()};
  falseExp        -> 'false' {'exp.False'()};
  strExp          -> '\'' cs=(not(39) stringChar)* 39 {'exp.Str'(cs)};
  string          -> '\'' cs=(not(39) stringChar)* 39 {'values.Str'(cs)};
  stringChar      -> 92 c=. ! {c} | .;
  intExp          -> whitespace i=[48,57]+ {'exp.Int'(i)};
  int             -> whitespace i=[48,57]+ {'values.Int'(i)};
  alphaChar       -> [65,90] | [97,122];
  numChar         -> [48,57];
  caseExp         -> whitespace 'case' es=args whitespace '{' as=arms whitespace '}' {'exp.Case'(es,as)};
  arms            -> a=arm as=(';' arm)* {a:as};
  arm             -> ps=patterns whitespace '->' e=exp {'exp.Arm'(ps,e)};
  pattern         -> d=dataPattern (whitespace 'when' e=exp {'patterns.Cond'(d,e)} | {d});
  dataPattern     -> a=atomicPattern (whitespace ':' t=pattern {'patterns.Cons'(a,t)} | {a});
  atomicPattern   -> cnstrPattern | varPattern | constPattern | listPattern | recordPattern | parenPattern;
  parenPattern    -> whitespace '(' p=pattern whitespace ')' {p};
  varPattern      -> n=name (whitespace ':=' p=pattern {'patterns.Bind'(n,p)} | {'patterns.Var'(n)});
  constPattern    -> strPattern | boolPattern | intPattern;
  strPattern      -> '\'' cs=(not(39) .)* 39 {'patterns.Str'(cs)};
  boolPattern     -> truePattern | falsePattern;
  truePattern     -> whitespace 'true' {'patterns.True'()};
  falsePattern    -> whitespace 'false' {'patterns.False'()};
  intPattern      -> whitespace i=[48,57]+ {'patterns.Int'(i)};
  listPattern     -> whitespace '[' ps=patterns whitespace ']' {'patterns.List'(ps)};
  patterns        -> somePatterns | {[]};
  somePatterns    -> p=pattern ps=(',' pattern)* {p:ps};
  recordPattern   -> whitespace '{' fs=fieldPatterns whitespace '}' {'patterns.Record'(fs)};
  fieldPatterns     -> somefieldPatterns | {[]};
  somefieldPatterns -> f=fieldPattern fs=(';' fieldPattern)* {f:fs};
  fieldPattern      -> n=name whitespace '=' p=Pattern {'patterns.Field'(n,p)};
  cnstrPattern      -> n=name whitespace '(' ps=patterns whitespace ')' {'patterns.Cnstr'(n,ps)};
  keyWord           -> key not([97,122] | [65,90]);
  key               -> 'EOF' | 'thunk' | 'not' | 'and' | 'or' | 'when' | 'case' | 'module' | 'export' | 'import' | 'true' | 'false' | 'fun' | 'let' | 'in' | 'letrec' | 'if' | 'then' | 'else';
  name              -> whitespace not(keyWord) c=alphaChar chars=(alphaChar | numChar)* ! {'values.Str'(c:chars)};
  grammar           -> '{' rs=rules '}' ! {'grammar.Grammar'('',rs)};
  rule              -> n=name as=ruleArgs '->' b=pTerm {'grammar.Rule'(n,['grammar.Body'(as,b)])};
  rules             -> r=rule rs=(';' ! rule)* {r:rs};
  ruleArgs          -> someRuleArgs | {[]};
  someRuleArgs      -> '(' as=somePatterns ')' {as};
  pTerm             -> l=seq ('|' ! r=pTerm {'grammar.Or'(l,r)} | {l});
  seq               -> l=bind (r=seq {'grammar.Seq'(l,r)} | {l});
  bind              -> n=atomicPattern '=' ! a=repeat {'grammar.Bind'(n,a)} | repeat;
  repeat            -> x=star ! {x} | x=plus ! {x} | x=pAtom ! {x};
  star              -> a=pAtom '*' {'grammar.Star'(a)};
  plus              -> a=pAtom '+' {'grammar.Plus'(a)};
  pAtom             -> endOfInput | cut | pred | charCode | pDelay | lDelay | notTerm | call | eCall | term | range | dot | action | '(' a=pTerm ')' {a};
  cut               -> '!' {'grammar.Cut'()};
  pDelay            -> 'PDELAY' '(' start=string ',' end=string ',' g=exp ')' {'grammar.PDelay'(start,end,g)};
  lDelay            -> 'LDELAY' '(' start=string ',' end=string ',' g=exp ')' {'grammar.LDelay'(start,end,g)};
  call              -> n=name as=callArgs ! {'grammar.Call'(n,as)};
  callArgs          -> someCallArgs | {[]};
  someCallArgs      -> '^' '(' as=args ')' {as};
  eCall             -> '<' e=exp '>' {'grammar.EvalCall'(e,[])};
  term              -> '\'' cs=(not(39) stringChar)* 39 ! {'grammar.Term'(cs)};
  range             -> '[' l=(int | charX) ',' u=(int | charX) ']' {'grammar.Range'(l,u)};
  dot               -> '.' {'grammar.Dot'()};
  pred              -> whitespace '?' n=atom '(' as=args ')' {'grammar.Predicate'(n,as)};
  notTerm           -> whitespace 'not' '(' t=pTerm ')' {'grammar.Not'(t)};
  char              -> charLiteral | charCode;
  charCode          -> c=int {'grammar.Char'(c)};
  charLiteral       -> 39 x=. 39 {'grammar.Char'(x)};
  charX             -> 39 x=. 39 {x};
  endOfInput        -> 'EOF' {'grammar.EOF'()};
  action            -> '{[]}' ! { 'grammar.Const'('values.List'([])) } | '{' e=exp '}' {'grammar.Action'(e)}
}

XPL = letrec exp = xplBase(thunk(exp),{atom={}}) in exp

test() = 
  letrec
    extra(exp) = { start -> '?' e = exp { [| print(${e}) |] } }
    exp = xplBase(thunk(exp),{atom=extra(thunk(exp))})
  in intern exp {
       ???????10
     }