Genejector
==========

Genejector is a project I did for my computer science bachelor thesis. It's all about doing crazy new things with [genetic programming](https://en.wikipedia.org/wiki/Genetic_programming) in Java. This project is neither active nor maintained, but merely available here for its conceptual value and curious eyes.

Project synopsis
-----
The foundation of this project is the ﬁeld of genetic programming. Genetic programming is an interesting
combination of computer science and biology, in which programmers write caretaker software to create and
evolve masses of automatically generated programs. Inspired by evolution, these programs are mated and
mutated through numerous generations while enduring constant selection pressure by the caretaker to solve a
diﬃcult programming problem. This complex process is all put into action in the hope that, at some point, one
of the programs will excel at solving the problem. Classic targets of genetic programming include optimization
problems, artiﬁcial intelligence and novel algorithms. The goal of this project is to explore the possibility of
introducing several modern programming concepts to the ﬁeld, which is otherwise dominated by conventional
mathematical methodologies.

I seek to implement a framework to explore the following concepts in relation to genetic programming:

* **Context reﬂection**:
Genetic programs are traditionally expected to be completely self-contained and only take advantage of
custom crafted components. This limitation is problematic for problems which are not easily isolated but
are part of a larger codebase and thus very context-dependent. Implementation of a framework capable of
reﬂecting upon the problem context and reuse the codebase could signiﬁcantly ease the barriers of using
genetic programming to solve these problems.

* **Data structures**:
Modern programming has heavy reliance on data structures. However, these are rarely available in genetic
programming frameworks unless the genetic programs evolve them on their own. Common data structures
like lists, maps and sets could be introduced, and these are often available in the programming language
of choice already. Leveraging these could open up an opportunity for shorter and more readable programs
while taking advantage of proven and eﬃcient ways of managing data at runtime.

* **On-the-ﬂy compilation and injection**:
Genetic individuals are traditionally evaluated by custom evaluation functions coded into the custom
crafted components. By taking advantage of on-the-ﬂy compilation and injection, the individuals can be
evaluated in the original problem codebase while relieving the programmer of tedious work.

* **Execution sandboxing**:
To support modern programming features such as data structures and loops, genetic programs need to be
able to either directly or indirectly allocate memory as well as execute code that might not terminate. This
implies that genetic programs can act maliciously by, among other things, consuming a critical amount
of system resources. Execution sandboxing is an interesting solution to the issue that gives the genetic
program free rein to consume resources and perform actions while the framework can enforce hard limits
on consumption and protect the host system from damage.
