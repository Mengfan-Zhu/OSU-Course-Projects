import socket
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
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.bind((host, port))
        # initialize variables
        self.files = {}  # dictionary for file information. key is filename, value is a list of addresses for download

    # handle received data
    # words is a list of received words, addr is the address of the socket sending the data
    def handle_recv(self, words, addr):
        # loop for words list
        i = 0
        while i < len(words):
            # get the command keyword
            command = words[i]
            i += 1
            # deal with share command
            if command == "SHARE":
                # The message should begin with "SHARE" and end with "SHAREEND" to tell the message bound
                # Each word is a filename
                while words[i] != "SHAREEND":
                    filename = words[i]
                    # Check if the filename already exist
                    # if so, just add the new address to the value list; if not add a new pair
                    if filename in self.files:
                        self.files[filename].append(addr)
                    else:
                        self.files[filename] = [addr]
                    i += 1
                i += 1
            # deal with list command
            elif command == "LIST":
                # Arrange the message, add all filenames to the message.
                # The message should begin with "LIST" and end with "LISTEND" to tell the message bound
                msg = "LIST "
                for filename in self.files:
                    msg = msg + filename + " "
                msg += "LISTEND"
                # send back message
                self.socket.sendto(msg.encode(), addr)
            # deal with search command
            elif command == "SEARCH":
                # get filename to search
                filename = words[i]
                # The message should begin with "SEARCH"
                msg = "SEARCH "
                # check is the filename in the dictionary, and add corresponding message
                if filename in self.files:
                    msg += "True"
                else:
                    msg += "False"
                i += 1
                # send back message
                self.socket.sendto(msg.encode(), addr)
            # deal with address commend, which asking for address to download
            elif command == "ADDRESS":
                # get filename to download
                filename = words[i]
                # The message should begin with "ADDRESS", followed by the filename
                msg = "ADDRESS " + filename + " "
                # Add the socket address for download
                msg = msg + str(self.files[filename][0][0]) + " " + str(self.files[filename][0][1])
                i += 1
                # send back message
                self.socket.sendto(msg.encode(), addr)

    # run the server, receive coming data
    def run(self):
        while True:
            data, addr = self.socket.recvfrom(1024)
            addr = (addr[0], int(addr[1]))
            # if data not empty, handle it
            if data:
                # split data into words list, adn handle it
                self.handle_recv(data.decode().split(), addr)


# run the server
if __name__ == '__main__':
    ftps = Server()
    ftps.run()
