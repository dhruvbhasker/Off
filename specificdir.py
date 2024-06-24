import paramiko
from scp import SCPClient

def create_ssh_client(server, port, user, password):
    client = paramiko.SSHClient()
    client.load_system_host_keys()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(server, port, user, password)
    return client

def copy_files_from_remote_to_local(ssh_client, remote_path, local_path):
    with SCPClient(ssh_client.get_transport()) as scp:
        scp.get(remote_path, local_path, recursive=True)

# Define your server credentials and paths
server = 'your_unix_server_address'
port = 22
user = 'your_username'
password = 'your_password'
remote_path = '/path/to/remote/directory/'  # Use '' to copy all files
local_path = '/path/to/local/directory/'

# Connect to the server
ssh_client = create_ssh_client(server, port, user, password)

# Copy the files
copy_files_from_remote_to_local(ssh_client, remote_path, local_path)

# Close the SSH connection
ssh_client.close()
