# ChoreoPRISM: Probabilistic System Modeling Repository

Welcome to the ChoreoPRISM repository! This repository hosts a unified framework for modeling and analyzing concurrent probabilistic systems. 


## Abstract

We present a choreographic framework for modelling and analysing
  concurrent probabilistic systems based on the PRISM
  model-checker. This is achieved through the development of a
  choreography language, that is a specification language that allows
  to describe the desired interactions within a concurrent system from
  a global viewpoint. In particular, employing choreographies provides a clear and comprehensive view of system interactions, enabling the discernment of process flow and detection of potential errors, thus ensuring accurate execution and enhancing system reliability. We equip our language with a probabilistic semantics
  and then define a formal encoding into the PRISM language and
  discuss its correctness. Properties of programs written in our
  choreographic language can be model-checked by the PRISM
  model-checker via their translation into the PRISM language.
  Finally, we implement a compiler for our language and demonstrate
  its practical applicability via examples drawn from the use cases
  featured on the PRISM website.


## Usage

To use this framework, follow these steps:

1. Clone the repository to your local machine: git clone https://github.com/adeleveschetti/ChoreoPRISM.git

2. Navigate to the repository directory: cd code/src/

3. Execute the code by running the `Main.java` file: java Main.java

4. The generated PRISM code will be available in the `generated-sources` folder.


## Contact

If you have any questions or suggestions, feel free to contact us at [adele.veschetti@tu-darmstadt.de].

