import re
import csv
import os

# Function to read directories and file processing mode from a configuration file
def read_config(config_file_path):
    directory_pairs = []
    single_file_info = {}
    with open(config_file_path, 'r') as file:
        lines = file.readlines()
        input_dir = None
        output_dir = None
        for line in lines:
            if line.startswith("input_dir:"):
                input_dir = line.split(":", 1)[1].strip()
            elif line.startswith("output_dir:"):
                output_dir = line.split(":", 1)[1].strip()
                if input_dir and output_dir:
                    directory_pairs.append((input_dir, output_dir))
                    input_dir = None
                    output_dir = None
            elif line.startswith("mul_file:"):
                single_file_info['mul_file'] = line.split(":", 1)[1].strip()
            elif line.startswith("File_name:"):
                single_file_info['file_name'] = line.split(":", 1)[1].strip()
    return directory_pairs, single_file_info

# Define a function to parse the copybook file and extract field names and lengths
def parse_copybook(file_path):
    fields = []
    new = 0

    with open(file_path, 'r') as file:
        lines = file.readlines()
        for line in lines:
            # Remove trailing whitespaces
            line = line.rstrip()

            # Ignore lines containing an asterisk (*)
            if '*' in line:
                continue
            
            # Match lines with field definitions (e.g., 05 FIELD-NAME PIC X(10) or 05 FIELD-NAME PIC 9(10).)
            match = re.search(r'\w+\s+\d+\s+([\w-]+)\s+PIC\s+(\w)\((\d+)\)', line)
            if match:
                field_name = match.group(1)
                field_type = match.group(2)
                field_length = new + int(match.group(3))
                fields.append((field_name, field_length))
                new = field_length
    
    return fields

# Define a function to write the fields and their lengths to a CSV file
def write_to_csv(fields, output_file_path):
    with open(output_file_path, 'w', newline='') as csvfile:
        csv_writer = csv.writer(csvfile)
        csv_writer.writerow(['Field Name', 'Length'])
        for field_name, field_length in fields:
            csv_writer.writerow([field_name, field_length])

# Define a function to process a single file
def process_single_file(input_file, output_dir):
    base_name = os.path.basename(input_file)
    output_file = os.path.join(output_dir, f"{os.path.splitext(base_name)[0]}.csv")

    # Parse the copybook and write the results to a CSV file
    fields = parse_copybook(input_file)
    write_to_csv(fields, output_file)

    print(f"CSV file with field names and lengths has been created: {output_file}")

# Define a function to process multiple files in multiple directories
def process_files(directory_pairs):
    for input_dir, output_dir in directory_pairs:
        input_files = [os.path.join(input_dir, f) for f in os.listdir(input_dir) if f.endswith('.cpy')]
        for input_file in input_files:
            process_single_file(input_file, output_dir)

# Main function to determine if input is a directory or single file based on config
def main(config_file_path):
    directory_pairs, single_file_info = read_config(config_file_path)
    mul_file = single_file_info.get('mul_file', 'N')
    file_name = single_file_info.get('file_name', '')

    if mul_file == 'Y' and file_name:
        # Process only the specified file
        for input_dir, output_dir in directory_pairs:
            input_file = os.path.join(input_dir, file_name)
            if os.path.isfile(input_file):
                process_single_file(input_file, output_dir)
            else:
                print(f"Specified file does not exist: {input_file}")
    else:
        # Process all files in the directories
        process_files(directory_pairs)

# Configuration file path
config_file_path = 'C:/Users/salon/OneDrive/Desktop/copybook/directories.txt'

# Process the files
main(config_file_path)
