import socket
import select
import csv
import sys


# A class of server
class Server:
    # initialize the server
    def __init__(self):
        # get host and port
        host = socket.gethostname()
        # print(host)  # uncomment to see hostname
        port = int(sys.argv[1])
        # set up the server socket
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind((host, port))
        self.socket.listen(10)
        # initialize variables
        self.users = {}  # dictionary for user information, key is username, value is password
        self.active_users = {}  # dictionary for active user, key is username, value is the socket object for connection
        self.inputs = [self.socket]  # list for inputs
        self.outputs = []  # list for outputs
        self.messages = {}  # dictionary for messages waiting to be sent, key is a socket object for the connection,
        # value is a list of messages

    # read the users info file and put username: password pair in the users dictionary
    def read_users(self):
        with open('users.csv') as f:
            f_csv = csv.reader(f)
            # read users by row
            for row in f_csv:
                if row:
                    self.users[row[0]] = row[1]  # row[0] is username, row[1] is password

    # close the connection, remove the socket object from inputs and outputs and then close it.
    def close_connection(self, s):
        if self.inputs:
            self.inputs.remove(s)
        if self.outputs:
            self.outputs.remove(s)
        s.close()

    # handle the list of readable, deal with connection requests and the data received
    def handle_input(self, readable):

        # loop for all sockets objects in readable list
        for s in readable:
            # deal with the connection requests from clients
            if s is self.socket:
                # accept connection
                connection, client_address = s.accept()
                connection.setblocking(0)
                # add the socket object for the connection into inputs and outputs list
                self.inputs.append(connection)
                self.outputs.append(connection)
                # add the connection to messages dict, the value is an empty list for messages
                self.messages[connection] = []
            # receive and handle data from clients
            else:
                # receive data
                data = s.recv(1024).decode()
                # if data not empty, handle it
                if data:
                    # split data into words list
                    words = data.split()
                    i = 0
                    # loop for words list
                    while i < len(words):
                        # get the command keyword
                        command = words[i]
                        if command == "register" or command == "login":
                            username = words[i + 1]
                            password = words[i + 2]
                            i += 3
                            # deal with register command
                            if command == "register":
                                # fail, username already exist
                                if username in self.users:
                                    self.messages[s].append("Register fail\n")
                                # success, add user info and send back message
                                else:
                                    self.users[username] = password
                                    # write a new user's info into file
                                    with open('users.csv', 'a', newline='') as f:
                                        f_csv = csv.writer(f)
                                        f_csv.writerow([username, password])
                                    self.messages[s].append("Register success\n")
                            # deal with login command
                            else:
                                # success, username exist, password is right and not an active user
                                if username in self.users and password == self.users[username] and \
                                        username not in self.active_users:
                                    self.active_users[username] = s
                                    self.messages[s].append("Login success\n")
                                # fail
                                else:
                                    self.messages[s].append("Login fail\n")
                        # deal with list command
                        elif command == "list":
                            i += 1
                            # Arrange the message, add all active users' username to the message.
                            # The message should begin with "List" and end with "LISTEND" to tell the message bound
                            msg = "List "
                            for user in self.active_users:
                                msg = msg + user + " "
                            msg += "LISTEND"
                            self.messages[s].append(msg)
                        # deal with message command
                        elif command == "message":
                            # get username of receiver
                            sender = words[i + 1]
                            receiver = words[i + 2]
                            # arrange the message. The message should include the sender's username.
                            # The message should begin with "MSG" and end with "MSGEND" to tell the message bound
                            msg = "MSG " + sender + " " + words[i + 3]
                            i += 4
                            while words[i] != "MSGEND":
                                msg = msg + " " + words[i]
                                i += 1
                            i += 1
                            msg = msg + " MSGEND"
                            # success, receiver is active user
                            if receiver in self.active_users:
                                self.messages[self.active_users[receiver]].append(msg)
                                # tell the sender the message sent success
                                self.messages[s].append("Message success\n")
                            # fail
                            else:
                                self.messages[s].append("Message fail\n")

                        # deal with logout commend
                        elif command == "logout":
                            username = words[i + 1]
                            # delete from active users and close connection
                            self.active_users.pop(username)
                            self.close_connection(s)
                            i += 2
                        # deal with close commend, that is deal with close connection for a client not login
                        elif command == "close":
                            i += 1
                            self.close_connection(s)

    # send messages in self.messages list
    def send(self, writable):
        # loop for all socket objects in writable list
        for s in writable:
            # while there is a message in the messages list, send it
            while len(self.messages[s]) > 0:
                msg = self.messages[s].pop()
                s.sendall(msg.encode())

    # deal with exceptional, remove socket object and close connection
    def handle_exceptional(self, exceptional):
        for s in exceptional:
            if s in self.inputs:
                self.inputs.remove(s)
            if s in self.outputs:
                self.outputs.remove(s)
            s.close()

    # run the server, deal with multiplexing I/O
    def run(self):
        while True:
            readable, writable, exceptional = select.select(self.inputs, self.inputs, self.inputs)
            server.handle_input(readable)
            server.send(writable)
            server.handle_exceptional(exceptional)


# run the server
if __name__ == '__main__':
    server = Server()
    server.read_users()
    server.run()
