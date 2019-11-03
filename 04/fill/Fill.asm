// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed.
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.
(LOOP)

  @i
  M=0

  @KBD
  D=M

  @DRAW_WHITE
  D;JEQ

  (DRAW_BLACK)
    @i
    D=M
    @8192
    D=D-A
    @LOOP
    D;JGE
    @i
    D=M
    @SCREEN
    A=A+D
    M=-1
    @i
    M=M+1
    @DRAW_BLACK
    0;JMP

  (DRAW_WHITE)
    @i
    D=M
    @8192
    D=D-A
    @LOOP
    D;JGE
    @i
    D=M
    @SCREEN
    A=A+D
    M=0
    @i
    M=M+1
    @DRAW_WHITE
    0;JMP

@LOOP
0;JMP
