import paramiko
import os

def ssh_connect_and_download(host, port, username, password, remote_dir, local_dir):
    # Create an SSH client
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    try:
        # Connect to the server
        ssh.connect(host, port, username, password)
        sftp = ssh.open_sftp()
        
        # Ensure the local directory exists
        if not os.path.exists(local_dir):
            os.makedirs(local_dir)
        
        # List files in the remote directory
        for file in sftp.listdir(remote_dir):
            remote_file_path = os.path.join(remote_dir, file)
            local_file_path = os.path.join(local_dir, file)
            
            # Download the file
            sftp.get(remote_file_path, local_file_path)
            print(f"Downloaded {remote_file_path} to {local_file_path}")
    
    except Exception as e:
        print(f"Failed to connect or download from {host}: {e}")
    
    finally:
        sftp.close()
        ssh.close()

# Server 1 details
server1_details = {
    'host': 'server1.example.com',
    'port': 22,
    'username': 'user1',
    'password': 'password1',
    'remote_dir': '/path/to/remote/dir1',
    'local_dir': '/path/to/local/dir1'
}

# Server 2 details
server2_details = {
    'host': 'server2.example.com',
    'port': 22,
    'username': 'user2',
    'password': 'password2',
    'remote_dir': '/path/to/remote/dir2',
    'local_dir': '/path/to/local/dir2'
}

# Download files from both servers
ssh_connect_and_download(**server1_details)
ssh_connect_and_download(**server2_details)
