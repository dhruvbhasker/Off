import os

def list_files(directory, file_extension):
    # List to store file names without extension
    files_list = []

    # Traverse through all files
    for root, dirs, files in os.walk(directory):
        for file in files:
            # Check the file format
            if file.endswith(file_extension):
                # Remove the file extension and append to the list
                files_list.append(os.path.splitext(file)[0])

    return files_list

def write_to_txt(files_list, output_dir, output_file):
    # Create the output directory if it doesn't exist
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    output_path = os.path.join(output_dir, output_file)

    with open(output_path, 'w') as f:
        # Join the file names with semicolon and write to file
        f.write("Extratname:"+';'.join(files_list))

def main():
    # Directory to search
    directory = 'C:/Users/salon/OneDrive/Desktop/copybook/inputs'
    # File format to search for
    file_extension = '.cpy'
    # Output directory
    output_dir = 'C:/Users/salon/OneDrive/Desktop/copybook'
    # Output file
    output_file = 'outget.txt'

    files_list = list_files(directory, file_extension)
    write_to_txt(files_list, output_dir, output_file)

if __name__ == '__main__':
    main()
