import csv
import re

def parse_copybook(copybook_file):
    fields = []

    with open(copybook_file, 'r') as file:
        lines = file.readlines()

    for line in lines:
        line = line.rstrip()
        if is_commented(line):
            continue
        
        line = line.strip()
        if line.startswith(('01', '02', '03')):  # Adjust based on levels you want to parse
            parts = re.split(r'\s+', line)
            if len(parts) >= 3:
                level = parts[0]
                field_name = parts[1]
                field_definition = ' '.join(parts[2:])
                
                if 'PIC' in field_definition:
                    pic_match = re.search(r'PIC\s+(\S+)', field_definition)
                    if pic_match:
                        pic_clause = pic_match.group(1)
                        length = calculate_length_from_pic(pic_clause)
                        fields.append((field_name, length))
                    else:
                        print(f"No PIC clause found in line: {line}")
                else:
                    print(f"No PIC in field definition: {field_definition}")
            else:
                print(f"Line parts less than expected: {line}")
        else:
            print(f"Line does not start with a valid level: {line}")
                    
    return fields

def is_commented(line):
    """Determine if a line is commented out in COBOL copybook format."""
    # Check if the line starts with an asterisk in column 7 (6th index, 0-based)
    if len(line) > 6 and line[6] == '*':
        return True
    # Check if the line starts with an asterisk
    if line.strip().startswith('*'):
        return True
    return False

def calculate_length_from_pic(pic_clause):
    length = 0
    pic_clause = pic_clause.upper()
    
    if 'X' in pic_clause:
        x_match = re.search(r'X\((\d+)\)', pic_clause)
        if x_match:
            length = int(x_match.group(1))
        else:
            length = pic_clause.count('X')
    elif '9' in pic_clause:
        if '(' in pic_clause:
            length = int(re.search(r'9\((\d+)\)', pic_clause).group(1))
        else:
            length = len(re.findall(r'9', pic_clause))
        if 'V' in pic_clause:
            decimals = len(re.findall(r'9', pic_clause.split('V')[1]))
            length += decimals
    
    return length

def write_to_csv(fields, output_csv_file):
    with open(output_csv_file, 'w', newline='') as csvfile:
        csvwriter = csv.writer(csvfile)
        csvwriter.writerow(['Field Name', 'Length'])
        csvwriter.writerows(fields)

if __name__ == "__main__":
    copybook_file = 'path_to_copybook_file.cpy'
    output_csv_file = 'output.csv'
    
    fields = parse_copybook(copybook_file)
    write_to_csv(fields, output_csv_file)
    
    if fields:
        print(f"Data has been written to {output_csv_file}")
    else:
        print("No fields were found.")
