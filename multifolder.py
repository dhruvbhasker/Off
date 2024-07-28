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
    inside_05 = False
    current_05_field = None
    current_05_length = 0
    current_05_length_inside = 0

    new = 1

    with open(file_path, 'r') as file:
        lines = file.readlines()
        for line in lines:
            line = line.rstrip()

            if '*' in line:
                continue

            match_01 = re.fullmatch(r'\s+05\s+([\w-]+)\s+PIC\s+(\w)\((\d+)\).', line)
            match_05 = re.fullmatch(r'\s+05\s+([\w-]+).', line)
            match_10 = re.fullmatch(r'\s+10\s+([\w-]+)\s+PIC\s+(\w)\((\d+)\).', line)
            match_77 = re.fullmatch(r'\s*77\s+([\w-]+)\s+PIC\s+(\w)\((\d+)\)', line)

            if match_01 or match_77:
                if inside_05 and current_05_field:
                    fields.append((current_05_field, current_05_length))
                    current_05_length += current_05_length_inside
                    new += current_05_length_inside
                    current_05_length_inside = 0

                fields.append((match_01.group(1), new) if match_01 else (match_77.group(1), int(match_77.group(3))))
                new += int(match_01.group(3)) if match_01 else 0
                inside_05 = False
                current_05_field = None

            elif match_05:
                if inside_05 and current_05_field:
                    fields.append((current_05_field, current_05_length))
                current_05_field = match_05.group(1)
                current_05_length = new
                new = current_05_length

                inside_05 = True

            elif match_10 and inside_05:
                current_05_length_inside += int(match_10.group(3))
                new = current_05_length

        if inside_05 and current_05_field:
            fields.append((current_05_field, current_05_length))

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

    fields = parse_copybook(input_file)
    write_to_csv(fields, output_file)

    print(f"CSV file with field names and lengths has been created: {output_file}")

# Define a function to process multiple files in multiple directories
def process_files(directory_pairs):
    for input_dir, output_dir in directory_pairs:
        for root, _, files in os.walk(input_dir):
            rel_path = os.path.relpath(root, input_dir)
            current_output_dir = os.path.join(output_dir, rel_path)
            os.makedirs(current_output_dir, exist_ok=True)
            
            for file in files:
                if file.endswith('.cpy'):
                    input_file = os.path.join(root, file)
                    process_single_file(input_file, current_output_dir)

# Main function to determine if input is a directory or single file based on config
def main(config_file_path):
    directory_pairs, single_file_info = read_config(config_file_path)
    mul_file = single_file_info.get('mul_file', 'N')
    file_name = single_file_info.get('file_name', '')

    if mul_file == 'Y' and file_name:
        for input_dir, output_dir in directory_pairs:
            input_file = os.path.join(input_dir, file_name)
            if os.path.isfile(input_file):
                process_single_file(input_file, output_dir)
            else:
                print(f"Specified file does not exist: {input_file}")
    else:
        process_files(directory_pairs)

# Configuration file path
config_file_path = 'C:/Users/salon/OneDrive/Desktop/copybook/directories.txt'

# Process the files
main(config_file_path)
