import csv

# Define the original and modified data
original_data = [
    {"ID": 1, "Name": "Alice", "Age": 25, "City": "New York", "Occupation": "Engineer"},
    {"ID": 2, "Name": "Bob", "Age": 30, "City": "Los Angeles", "Occupation": "Doctor"},
    {"ID": 3, "Name": "Charlie", "Age": 35, "City": "Chicago", "Occupation": "Artist"},
    {"ID": 4, "Name": "David", "Age": 40, "City": "Lucknow", "Occupation": "Lawyer"},
    {"ID": 5, "Name": "Eve", "Age": 45, "City": "Phoenix", "Occupation": "Scientist"}
]

modified_data = [
    {"ID": 1, "Name": "Alice", "Age": 25, "City": "New York", "Occupation": "Engineer"},
    {"ID": 2, "Name": "Bob", "Age": 30, "City": "Los Angeles", "Occupation": "Doctor"},
    {"ID": 3, "Name": "Charlie", "Age": 35, "City": "Chicago", "Occupation": "Artist"},
    {"ID": 4, "Name": "David", "Age": 40, "City": "Lucknow", "Occupation": "Guitarist"},
    {"ID": 5, "Name": "Eve", "Age": 50, "City": "Phoenix", "Occupation": "intern"}
]

# Compare the original and modified data to find differences
differences = []
for orig, mod in zip(original_data, modified_data):
    for key in orig:
        if orig[key] != mod[key]:
            differences.append({"ID": orig["ID"], "Field": key, "Original": orig[key], "Modified": mod[key]})

# Create a CSV file with the differences
csv_file_path = "C:/Users/salon/OneDrive/Desktop/New folder/differences.csv"
with open(csv_file_path, "w", newline='') as csvfile:
    fieldnames = ["ID", "Field", "Original", "Modified"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    for difference in differences:
        writer.writerow(difference)
