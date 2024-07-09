import re
import csv

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

# File paths
copybook_file_path = 'C:/Users/salon/OneDrive/Desktop/CCSRSVE.cpy'
output_csv_file_path = 'C:/Users/salon/OneDrive/Desktop/fields_lengths.csv'

# Parse the copybook and write the results to a CSV file
fields = parse_copybook(copybook_file_path)
write_to_csv(fields, output_csv_file_path)

print(f"CSV file with field names and lengths has been created: {output_csv_file_path}")
