package com.example.panocartemtl.carte

import com.example.panocartemtl.carte.InterfaceCarte.IPAInterface
import com.example.panocartemtl.carte.InterfaceCarte.InitialisationInterface
import com.example.panocartemtl.carte.InterfaceCarte.MapboxInterface
import com.example.panocartemtl.carte.InterfaceCarte.MontreInterface
import com.example.panocartemtl.carte.InterfaceCarte.NavigationInterface
import com.example.panocartemtl.carte.InterfaceCarte.SpinnerInterface

// Inspiré des principes de conception, de syntaxe et d'organistion des codes de Rust
interface IPrésentateurCarte:
    InitialisationInterface,
    IPAInterface,
    MapboxInterface,
    MontreInterface,
    NavigationInterface,
    SpinnerInterface