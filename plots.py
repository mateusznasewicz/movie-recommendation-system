import matplotlib.pyplot as plt
import sys
import argparse

def plot_from_files(file_names, xlabel, ylabel, title):
    plt.figure(figsize=(10, 6))
    for file_name in file_names:
        try:
            data = []
            with open(file_name, 'r') as f:
                for line in f:
                    x, y = map(float, line.strip().split())
                    data.append((x, y))
            
            x_vals, y_vals = zip(*data)
            plt.plot(x_vals, y_vals, label=file_name.split("/")[1])
        
        except Exception as e:
            print(f"Nie udało się przetworzyć pliku {file_name}: {e}", file=sys.stderr)
    
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.legend()
    plt.grid(True)
    plt.savefig(f'wykresy/{title}', bbox_inches='tight', pad_inches=0)

if __name__ == "__main__":

    parser = argparse.ArgumentParser(description="Rysowanie wykresu na podstawie danych z plików.")
    parser.add_argument("files", nargs="+", help="Lista plików z danymi")
    parser.add_argument("--xlabel", type=str, default="Oś X", help="Etykieta osi X")
    parser.add_argument("--ylabel", type=str, default="Oś Y", help="Etykieta osi Y")
    parser.add_argument("--title", type=str, default="Tytuł", help="Tytuł wykresu")
    args = parser.parse_args()
    plot_from_files(args.files, args.xlabel, args.ylabel, args.title)
