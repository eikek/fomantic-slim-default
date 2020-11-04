let
  pkgs = import <nixpkgs> { };
in
with pkgs;

 mkShell {
   buildInputs = [
     nodejs
     sbt
     yarn
     lessc
   ];

 }
