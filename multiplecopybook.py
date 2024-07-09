import re
import csv
import os

# Define a function to parse the copybook file and extract field names and lengths
def parse_copybook(file_path):
    fields = []

    with open(file_path, 'r') as file:
        lines = file.readlines()
        for line in lines:
            # Remove trailing whitespaces
            line = line.rstrip()

            # Ignore lines containing an asterisk (*)
            if '*' in line:
                continue
            
            # Match lines with field definitions (e.g., 05 FIELD-NAME PIC X(10) or 05 FIELD-NAME PIC 9(10).)
            match = re.search(r'\d+\s+(\S+)\s+PIC\s+(\w)\((\d+)\)', line)
            if match:
                field_name = match.group(1)
                field_type = match.group(2)
                field_length = int(match.group(3))
                fields.append((field_name, field_length))
    
    return fields

# Define a function to write the fields and their lengths to a CSV file
def write_to_csv(fields, output_file_path):
    with open(output_file_path, 'w', newline='') as csvfile:
        csv_writer = csv.writer(csvfile)
        csv_writer.writerow(['Field Name', 'Length'])
        for field_name, field_length in fields:
            csv_writer.writerow([field_name, field_length])

# Define a function to process multiple files
def process_files(input_files, output_dir):
    for input_file in input_files:
        # Generate output file path
        base_name = os.path.basename(input_file)
        output_file = os.path.join(output_dir, f"{os.path.splitext(base_name)[0]}_fields_lengths.csv")

        # Parse the copybook and write the results to a CSV file
        fields = parse_copybook(input_file)
        write_to_csv(fields, output_file)

        print(f"CSV file with field names and lengths has been created: {output_file}")

# List of input copybook files
input_files = [
    'C:/Users/salon/OneDrive/Desktop/CCSRSVE1.cpy',
    'C:/Users/salon/OneDrive/Desktop/CCSRSVE2.cpy'
]

# Output directory
output_dir = 'C:/Users/salon/OneDrive/Desktop/'

# Process the files
process_files(input_files, output_dir)
