// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.

// Computes R0 = 2 + 3  (R0 refers to RAM[0])
      
@-32768
D=A
@3
D=D+A;JLE
D // fme
D+A
@0
M=D