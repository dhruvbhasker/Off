import paramiko
from scp import SCPClient
import socket

def create_ssh_client(server, port, user, password):
    try:
        client = paramiko.SSHClient()
        client.load_system_host_keys()
        client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        client.connect(server, port, user, password)
        print(f"SSH connection established to {server}.")
        return client
    except socket.gaierror as e:
        print(f"Address-related error connecting to server {server}: {e}")
        raise
    except paramiko.AuthenticationException as e:
        print(f"Authentication failed for server {server}: {e}")
        raise
    except Exception as e:
        print(f"Error connecting to server {server}: {e}")
        raise

def copy_files_from_remote_to_local(ssh_client, remote_path, local_path):
    try:
        with SCPClient(ssh_client.get_transport()) as scp:
            scp.get(remote_path, local_path, recursive=True)
            print("Files copied successfully.")
    except Exception as e:
        print(f"Error during file transfer: {e}")
        raise

def transfer_files_from_server(server, port, user, password, remote_path, local_path):
    try:
        # Connect to the server
        ssh_client = create_ssh_client(server, port, user, password)
        # Copy the files
        copy_files_from_remote_to_local(ssh_client, remote_path, local_path)
    finally:
        # Close the SSH connection
        if 'ssh_client' in locals():
            ssh_client.close()
            print(f"SSH connection to {server} closed.")

# Define your servers, credentials, and paths
servers = [
    {
        'server': 'server1_address',
        'port': 22,
        'user': 'user1',
        'password': 'password1',
        'remote_path': '/path/to/remote/directory1/*',
        'local_path': '/path/to/local/directory1/'
    },
    {
        'server': 'server2_address',
        'port': 22,
        'user': 'user2',
        'password': 'password2',
        'remote_path': '/path/to/remote/directory2/*',
        'local_path': '/path/to/local/directory2/'
    }
]

# Transfer files from both servers
for server_info in servers:
    transfer_files_from_server(
        server_info['server'],
        server_info['port'],
        server_info['user'],
        server_info['password'],
        server_info['remote_path'],
        server_info['local_path']
    )
