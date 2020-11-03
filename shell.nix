let
  pkgs = import <nixpkgs> { };
in
with pkgs;

 mkShell {
   buildInputs = [
     nodejs
     sbt
     yuicompressor
     yarn
     lessc
   ];

 }
