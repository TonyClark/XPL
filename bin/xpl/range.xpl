export test1,test2,test3,range

less(n,m) = n < m
equal(n,m) = n = m

range = {
  start            -> '[' low=num ',' high=num ']' createRange^(low,high);
  createRange(n,m) -> ?less(n,m) { {low=n;high=m} } | ?equal(n,m) { n } | { 'error' };
  num              ->  i=[48,57]+ { asInt(i) }
}

test1() =
  range.parse('[100,200]',[])
  
test2() =
  range.parse('[100,100]',[])
  
test3() =
  range.parse('[100,20]',[])
  
  
  
  