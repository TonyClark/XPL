export Action,Bind,Call,Char,Closure,Dot,Drop,EOF,Grammar,Lift,Not,Or,PDelay,Plus,PTerm,Range,Rule,Seq,Star,Term,subst

import 'src/xpl/lists.xpl'

Action         = 'grammar.Action'.java()
Bind           = 'grammar.Bind'.java()
Call           = 'grammar.Call'.java()
Char           = 'grammar.Char'.java()
Closure        = 'grammar.Closure'.java()
Dot            = 'grammar.Dot'.java()
Drop           = 'grammar.Drop'.java()
EOF            = 'grammar.EOF'.java()
Grammar        = 'grammar.Grammar'.java()
Lift           = 'grammar.Lift'.java()
Not            = 'grammar.Not'.java()
Or             = 'grammar.Or'.java()
PDelay         = 'grammar.PDelay'.java()
Plus           = 'grammar.Plus'.java()
PTerm          = 'grammar.PTerm'.java()
Range          = 'grammar.Range'.java()
Rule           = 'grammar.Rule'.java()
Seq            = 'grammar.Seq'.java()
Star           = 'grammar.Star'.java()
Term           = 'grammar.Term'.java()

subst(new,old,value) =
  if equal(old,value)
  then new
  else 
    case value {
      Action(e) -> value
      Bind(name,term) ->
        Bind(n,subst(new,old,term))
      Call(name) -> value
      Char(c) -> value
      Closure(env,exp) -> value
      Dot() -> value
      Drop(term) ->
        Drop(subst(new,old,term))
      EOF() -> value
      ExtensionPoint(name) -> value
      Grammar(start,rules) ->
        Grammar(start,map(fun(rule) subst(new,old,rule),rules))
      Lift(term) ->
        Lift(subst(new,old,term))
      Not(term) ->
        Not(subst(new,old,term))
      Or(left,right) ->
        Or(subst(new,old,left),subst(new,old,right))
      PDelay(start,end,grammar,term) ->
        PDelay(start,end,grammar,subst(new,old,term))
      Plus(term) ->
        Plus(subst(new,old,term))
      Range(l,h) -> value
      Rule(name,args,body) ->
        Rule(name,args,subst(new,old,body))
      Seq(first,second) ->
        Seq(subst(new,old,first),subst(new,old,second))
      Star(term) ->
        Star(subst(new,old,term))
      Term(t) -> value
      x -> x
  }
  
equal(v1,v2) =
  if v1 = v2
  then true
  else
    case v1,v2 {
      Action(e1),Action(e2) -> false
      Bind(n1,t1),Bind(n2,t2) ->
        n1 = n2 and
        equal(t1,t2)
      Call(n1,as1),Call(n2,as2) ->
        n1 = n2 and
        equal(as1,as2)
      Char(c1),Char(c2) ->
        c1 = c2
      Dot(),Dot() ->
        true
      Drop(term1),Drop(term2) ->
        equal(term1,term2)
      EOF(),EOF() ->
        true
      ExtensionPoint(name1),ExtensionPoint(name2) ->
        name1 = name2
      Grammar(start1,rules1),Grammar(start2,rules2) ->
        equal(start1,start2) and
        equal(rules1,rules2)
      Lift(term1),List(term2) ->
        equal(term1,term2)
      Not(term1),Not(term2) ->
        equal(term1,term2)
      Or(left1,right1),Or(left2,right2) ->
        equal(left1,left2) and
        equal(right1,right2)
      PDelay(start1,end1,grammar1,term1),PDelay(start2,end2,grammar2,term2) ->
        start1 = start2 and
        end1 = end2 and
        grammar1 = grammar2 and
        equal(term1,term2)
      Plus(term1),Plus(term2) ->
        equal(term1,term2)
      Range(low1,high1),Range(low2,high2) ->
        low1 = low2 and
        high1 = high2
      Rule(name1,args1,body1),Rule(name2,args2,body2) ->
        equal(name1,name2) and
        equal(args1,args2) and
        equal(body1,body2)
      Seq(first1,second1),Seq(first2,second2) ->
        equal(first1,first2) and
        equal(second1,second2)
      Star(term1),Star(term2) ->
        equal(term1,term2)
      Term(t1),Term(t2) ->
        t1 = t2
      h1:t1,h2:t2 ->
        equal(h1,h2) and
        equal(t1,t2)
      any1,any2 -> false
    }