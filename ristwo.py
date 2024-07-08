import csv

def txt_to_array(file_path):
    try:
        with open(file_path, 'r') as file:
            lines = file.readlines()
        # Strip newline characters from each line
        array = [line.strip() for line in lines]
        return array
    except FileNotFoundError:
        print(f"File not found: {file_path}")
        return []

def selected_lines_to_csv(array, selected_indices, output_csv):
    selected_lines = [array[i] for i in selected_indices if i < len(array)]
    with open(output_csv, 'w', newline='') as csvfile:
        csvwriter = csv.writer(csvfile)
        # Writing each selected line as a new row in the CSV
        for line in selected_lines:
            csvwriter.writerow([line])

# Example usage:
file_path = 'example.txt'
output_csv = 'output.csv'
array = txt_to_array(file_path)

# Select the indices of the lines you want to include in the CSV (e.g., first, third, and fifth lines)
selected_indices = [0, 2, 4]  # Indices start at 0
selected_lines_to_csv(array, selected_indices, output_csv)
print(f"Selected lines have been written to {output_csv}")
