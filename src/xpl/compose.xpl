export test1,test2,test3,test4,test5,test6,test7,test8

g1 = { start1 -> 'x' { 10 } }
g2 = { start2 -> 'y' { 20 } }

g3 = { start -> 'x' { 10 } }
g4 = { start -> 'y' { 20 } }

test1() =
  (g1+g2).parse('x',[])
  
test2() =
  (g1+g2).parse('y',[])
  
test3() =
  (g2+g1).parse('x',[])
  
test4() =
  (g2+g1).parse('y',[])
  
test5() =
  (g3+g4).parse('x',[])
  
test6() =
  (g3+g4).parse('y',[])
  
test7() =
  (g4+g3).parse('x',[])
  
test8() =
  (g4+g3).parse('y',[])
  
  
  