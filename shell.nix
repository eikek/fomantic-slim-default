let
  pkgs = import <nixpkgs> { };
in
with pkgs;

 mkShell {
   buildInputs = [
     nodejs
     yuicompressor
     yarn
     lessc
   ];

 }
