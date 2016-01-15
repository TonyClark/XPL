export test1,test2,customer,inflator

// We need syntax constructors for Record and Field:

import 'src/xpl/exp.xpl'

// We need list operations: 
//    take([1,2,3],2) = [1,2]
//    drop([1,2,3],2) = [3]
//    foldr(f,g,b,[1,2,3]) = g(f(1),g(f(2),g(f(3),b)))

import 'src/xpl/lists.xpl'

// Define some functions to be used as args to foldr:

combine(left,right) = [| fun(l) ${left}(l,fun(r,l) r + ${right}(l)) |]
id(x) = x
empty = [| fun(l) {} |]

// Define a function that constructs *the syntax* of a field extractor:
//    extractor('name',5) = [| fun(l,k) k({name=asString(take(l,5))},drop(l,5)) |]

extractor(n,i) = 
  let record = [| { ${n} = asString(take(l,${i})) } |]
  in [| fun(l,k) k(${record},drop(l,${i})) |]
  
// Define the grammar to consists of a sequence of fields.
// Each field builds an extractor. All extractors are combined
// into a mapping from a sequence of character codes to a record:
  
inflator = {
  fields -> fs=field* { foldr(id,combine,empty,fs) };
  field -> n=name whitespace ':' i=int { extractor(n,i) };
  int -> whitespace n=numeric+ { Int(asInt(n)) };
  whitespace -> (32 | 10 | 9 | 13)*;  
  name   -> whitespace l=alpha ls=alpha* { asString(l:ls) };  
  alpha  -> ['a','z'];
  numeric -> ['0','9']
}

// Here is a use of the language: a customer is a name (5 chars) followed
// by an address (15 chars), follower by an account number (3 chars):

customer = 
  intern inflator {
    customer:5
    address:15
    account:3
  }
  
// The customer map is used by applying it to a stream of char codes:

input = [102,114,101,100,32,49,48,32,77,97,105,110,32,82,111,97,100,32,32,32,53,48,49]
  
test1() = customer(input)

// Just to show everything is first-class:

test2() = map(customer,repeat(input,10))