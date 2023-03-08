#include <bits/stdc++.h>

using namespace std;

// Función para limpiar texto, conservando letras, números, espacios y saltos de línea, convertir a minúsculas
string limpiar_texto(string texto) {
    string texto_limpio;
    for (int i = 0; i < texto.length(); i++) {
        if (isalnum(texto[i]) || texto[i] == ' ' || texto[i] == '\t') {
            texto_limpio += tolower(texto[i]);
        }
    }
    return texto_limpio;
}

// Main: leer archivo, limpiar texto, guardar texto limpio en archivo 
int main(int argc, char *argv[]) {
    if (argc != 2) {
        cout << "Uso: " << argv[0] << " <archivo>" << endl;
        return 1;
    }
    ifstream archivo_entrada(argv[1]);
    if (!archivo_entrada) {
        cout << "No se pudo abrir el archivo " << argv[1] << endl;
        return 1;
    }
    string texto;
    string texto_limpio;
    while (getline(archivo_entrada, texto)) {
        texto_limpio += limpiar_texto(texto) + '\n';
    }
    archivo_entrada.close();
    
    // Archivo de salida con el texto limpio con nombre <archivo>_limpio.txt
    string nombre_archivo_salida = argv[1];
    nombre_archivo_salida += "_limpio.txt";
    ofstream archivo_salida(nombre_archivo_salida);
    archivo_salida << texto_limpio;
    archivo_salida.close();

    return 0;

}