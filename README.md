import os
os.add_dll_directory('C:/Users/salon/AppData/Local/Programs/Python/Python312/Lib/site-packages/clidriver/bin')
import ibm_db


# Database connection parameters
dsn = (
    "DATABASE=your_database_name;"
    "HOSTNAME=your_host_name;"
    "PORT=your_port_number;"
    "PROTOCOL=TCPIP;"
    "UID=your_username;"
    "PWD=your_password;"
)

# Connect to the database
try:
    conn = ibm_db.connect(dsn, "", "")
    print("Connected to the database")
except Exception as e:
    print(f"Unable to connect to the database: {e}")
    exit(1)

# Query to get files from a specific position
# This is a placeholder query. You need to modify it according to your actual database schema.
position = 'your_position_criteria'
sql = f"SELECT file_id, file_name, file_data FROM your_table_name WHERE position_column = '{position}'"

# Directory to save the files
local_directory = '/path/to/your/local/directory'

# Ensure the local directory exists
if not os.path.exists(local_directory):
    os.makedirs(local_directory)

try:
    stmt = ibm_db.exec_immediate(conn, sql)
    result = ibm_db.fetch_assoc(stmt)

    while result:
        file_id = result['file_id']
        file_name = result['file_name']
        file_data = result['file_data']

        # Define the full path to save the file
        file_path = os.path.join(local_directory, file_name)

        # Write file data to local file system
        with open(file_path, 'wb') as file:
            file.write(file_data)

        print(f"File {file_name} (ID: {file_id}) saved to {file_path}")
        
        # Fetch the next row
        result = ibm_db.fetch_assoc(stmt)

except Exception as e:
    print(f"Error retrieving or saving files: {e}")

# Close the database connection
ibm_db.close(conn)
