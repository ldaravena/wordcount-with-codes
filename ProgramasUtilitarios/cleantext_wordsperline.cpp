// C++ program to read a text file and remove all the special characters, keeping only the alphabets and numbers,
// lowercasing the alphabets, write n words per line and write the output to a new file.

#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <algorithm>
#include <iterator>
#include <cctype>

using namespace std;

int main(int argc, char* argv[])
{
    ifstream infile(argv[1]);
    string outfile_name = argv[1] + string("_clean");
    ofstream outfile(outfile_name);
    int n = stoi(argv[2]); // number of words per line
    string line;
    string word;
    vector<string> words;

    while (getline(infile >> ws, line)) {
        istringstream iss(line);
        while (iss >> word) {
            // remove all non-alphanumeric characters
            word.erase(remove_if(word.begin(), word.end(), [](char c) { return !isalnum(c); }), word.end());
            // lowercase the word
            transform(word.begin(), word.end(), word.begin(), ::tolower);
            words.push_back(word);
        }

        // write n words per line
        for (int i = 0; i < words.size(); i++) {
            outfile << words[i] << " ";
            if ((i + 1) % n == 0) {
                outfile << endl;
            }
        }

        // clean the vector
        words.clear();
    }

    infile.close();

    return 0;
}