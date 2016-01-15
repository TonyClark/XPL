export map,length,remove,product,select,elt,exists,flatten,append,filter,sort,removeIf,take,drop,foldr,repeat

map(f,l) = 
  case l {
    [] -> [];
    h:t -> f(h) : map(f,t)
  }
  
remove(x,l) =
  case l {
    [] -> [];
    y:l ->
      if x = y
      then remove(x,l)
      else y:(remove(x,l))
  }
  
length(l) =
  case l {
    [] -> 0;
    h:t -> 1 + (length(t))
  }
  
product(l1,l2) =
  case l1,l2 {
    a:b,x:y -> [a,x]:product(b,y);
    x,y -> []
  }
  
select(pred,l,default) =
  case l {
    [] -> default;
    x:l -> 
      if pred(x)
      then x
      else select(pred,l,default)
  }
  
exists(pred,l) =
  case l {
    [] -> false;
    x:l -> 
      if pred(x)
      then true
      else exists(pred,l)
  }
  
elt(n,l) =
  case n,l {
    0,x:l -> x;
    n,[] -> 'error';
    n,x:l -> elt(n-1,l)
  }
  
flatten(lists) =
  case lists {
    l:ls ->
      append(l,flatten(ls));
    [] -> []
  }
  
append(l1,l2) =
  case l1 {
    x:l1 ->
      x : (append(l1,l2));
    [] -> l2
  }
  
filter(pred,list) =
  case list {
    [] -> [];
    x:list ->
      if pred(x)
      then x:filter(pred,list)
      else filter(pred,list)
  }
  
removeIf(list,pred) =
  case list {
    [] -> [];
    x:list ->
      if pred(x)
      then removeIf(list,pred)
      else x:removeIf(list,pred)
  }
  
sort(compare,l) = 
  case l {
    [] -> [];
    h:t ->
      let pre = filter(fun(x) compare(x,h),l)
          post = filter(fun(x) compare(h,x) and x != h,l)
      in sort(compare,pre) + [h] + sort(compare,post)
  }
  
take(list,n) =
  case n {
    0 -> [];
    n -> case list { h:t -> h:take(t,n-1) }
  }
  
drop(list,n) =
  case n {
    0 -> list;
    n -> case list { h:t -> drop(t,n-1) }
  }
  
foldr(f,g,b,l) =
  case l {
    [] -> b;
    h:t -> g(f(h),foldr(f,g,b,t))
  }
      
repeat(list,n) =
  case n {
    0 -> [];
    n -> list:repeat(list,n-1)
  }    