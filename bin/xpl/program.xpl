export test

import 'src/xpl/exp.xpl'
import 'src/xpl/xpl.xpl'

fields = XPL.fields

library = { 
  library -> h = header fs = fields  { 
    [| { header = ${h.lift()}; defs = ${Record(fs)} } |] 
  }; 
  header -> 'header' s=.* 'end' { asString(s) }
}

test() =
  intern library {
    header
      This library performs arithmetic.
    end
    add(n,m) = n + m;
    sub(n,m) = n - m
  }
