with import <nixpkgs>{}; 

stdenv.mkDerivation rec {
  name = "shell";
  buildInputs = [ pkgs.maven3 ];
}
