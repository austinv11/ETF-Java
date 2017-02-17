# ETF-Java
A Java parser/writer for the ETF format.

This api aims to provide high speed low and high level abstractions
for ETF. Additionally it supports multiple subsets of ETF: standard ETF,
BERT-rpc and Hammer & Chisel's (Discord's) Loqui.

## Using ETF-Java
**TODO**

## A note about performance
While this project attempts to be as well optimized as possible, the JVM
does however creates a limit to how fast this can be just due to how it 
manages memory. 

Performance can be improved via JNI or usage of the `Unsafe` class
but these have issues which has kept me using pure java. 

Regarding JNI: I do not know enough C/C++ to properly optimize and there
would still be a cost for interfacing natives with java (meaning that for 
smaller ETF data there won't be much of a benefit).

Regarding `Unsafe`: Unsafe is *unsafe*. Meaning it is not guaranteed to 
work across java versions and jvm implementations. So despite its 
significant performance benefits, it's impossible to be sure that it will
always work how I expect it to.

## Caveats
Due to java not implementing proper primitive unsigned data types, I have
been forced to use much larger data types than should be necessary to 
prevent potential underflows (ex. `long` instead of `int` or `BigInteger` 
instead of `long`). This causes some strange internal data type handling
and some slight overhead.

## Helpful Resources
* [ETF Documentation](http://erlang.org/doc/apps/erts/erl_ext_dist.html)
* [BERT Documentation](http://bert-rpc.org/)
* [Loqui](https://github.com/hammerandchisel/loqui)