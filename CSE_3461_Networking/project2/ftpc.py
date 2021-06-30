import os
import socket
import sys
import tkinter as tk
import tkinter.messagebox


# a class for client
class Client:
    # initialize the client
    def __init__(self):
        # get client host and port
        self.host = socket.gethostname()
        self.port = int(sys.argv[3])
        # set up the client socket
        self.server_addr = (sys.argv[1], int(sys.argv[2]))
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.bind((self.host, self.port))
        # set up timeout for receiving data
        self.socket.settimeout(0.1)
        # initialize variables
        self.current_window = None  # the current gui window
        self.after_id = None  # the after_id for timing operation
        self.fp_write = None # the write file object
        self.file_size = None # the expect file size

    # close socket and gui window
    def close(self):
        self.current_window.destroy()
        self.socket.close()

    # close previous window
    def close_previous(self):
        self.current_window.after_cancel(self.after_id)
        self.current_window.destroy()

    # set a button
    def set_button(self, name):
        btn = tk.Button(self.current_window, text=name, command=lambda: self.choose_action(name))
        btn.pack()

    # common setup for creating a window
    def common_create_window(self):
        # create new window
        self.current_window = tk.Tk()
        # set up title
        title = "client - (" + self.host + ", " + str(self.port) + ")"
        self.current_window.title(title)
        # set up size
        self.current_window.geometry('500x200')
        # receiving data every 200ms
        self.after_id = self.current_window.after(200, self.receive)
        # set close window event
        self.current_window.protocol('WM_DELETE_WINDOW', self.close)

    # the menu window, including Share, List, Search and Download button
    def menu(self):
        # close previous window if exist
        if self.current_window:
            self.close_previous()
        # create a new window
        self.common_create_window()
        # set buttons
        self.set_button("Share")
        self.set_button("List")
        self.set_button("Search")
        self.set_button("Download")
        # set mainloop to interactive with user
        self.current_window.mainloop()

    # the layout for input window, there is an input box. This layout is used for following windows:
    #   the input window for share
    #   the input window for search
    #   the input window for download
    def input_layout(self, flag, lbl_name):
        # close previous window
        self.close_previous()
        # create a new window
        self.common_create_window()
        # set input boxes
        lbl = tk.Label(self.current_window, text=lbl_name)
        lbl.pack()
        etr = tk.Entry(self.current_window, show=None)
        etr.pack()
        # set submit and return buttons
        submit_btn = tk.Button(self.current_window, text="Submit",
                               command=lambda: self.handle_submit(flag, etr.get()))
        submit_btn.pack()
        self.set_button("Return")
        # set mainloop to interactive with user
        self.current_window.mainloop()

    # the window for list
    def list_window(self, lists):
        # close previous window
        self.close_previous()
        # create a new window
        self.common_create_window()
        # show the list message
        lbl = tk.Label(self.current_window, text=lists)
        lbl.pack()
        # set return button
        self.set_button("Return")
        # set mainloop to interactive with user
        self.current_window.mainloop()

    # choose action for command buttons and return buttons by flag
    def choose_action(self, flag):
        if flag == "Share":
            self.input_layout(flag, "input filenames, separated by whitespace:")  # go to input window
        elif flag == "List":
            # send list command
            msg = "LIST"
            self.socket.sendto(msg.encode(), self.server_addr)
        elif flag == "Search" or flag == "Download":
            self.input_layout(flag, "filename")  # go to input window
        elif flag == "Return":
            self.menu()  # go to menu window

    # handle what will happen after click submit buttons
    def handle_submit(self, flag, ety):
        # check the input boxes are not empty
        if ety == "":
            tk.messagebox.showinfo(title="error", message="input box can't be empty")
        else:
            if flag == "Share":
                # send share message to server
                msg = "SHARE " + ety + " SHAREEND"
                self.socket.sendto(msg.encode(), self.server_addr)
                self.menu()
            elif flag == "Search":
                # send search message to server
                msg = "SEARCH " + ety
                self.socket.sendto(msg.encode(), self.server_addr)
            elif flag == "Download":
                # send message to ask download address from server
                msg = "ADDRESS " + ety
                self.socket.sendto(msg.encode(), self.server_addr)

    # send file
    def send_file(self, filename, addr):
        # get file path and open file
        path = "upload/" + filename
        fp = open(path, 'r')
        # get file size
        size = os.path.getsize(path)
        # begin with "FILE", first send filename and file size
        msg = "FILE " + filename + " " + str(size)
        self.socket.sendto(msg.encode(), addr)
        # send file content
        while size > 1024:
            msg = fp.read(1024)
            self.socket.sendto(msg.encode(), addr)
            size -= 1024
        msg = fp.read(size)
        self.socket.sendto(msg.encode(), addr)
        # send "FILEEND" indicating the file sending is end
        msg = "FILEEND " + filename
        self.socket.sendto(msg.encode(), addr)
        # close the file
        fp.close()

    # handle the received messages from server
    def handle_receive(self, data, addr):
        # split data to a list of words
        words = data.decode().split()
        # loop for words list
        for i in range(0, len(words)):
            word = words[i]
            i += 1
            # handle return message for list command
            if word == "LIST":
                # rebuild message, each word (a filename) in one line until find "LISTEND"
                lists = "Accessible Files:\n"
                while words[i] != "LISTEND":
                    lists = lists + words[i] + "\n"
                    i += 1
                i += 1
                # open list window
                self.list_window(lists)
            # handle return message for search command
            elif word == "SEARCH":
                # file exist
                if words[i] == "True":
                    reply = "This file is exist"
                # file not exist
                else:
                    reply = "This file is not exist"
                # pop up a message box to tell the result
                tk.messagebox.showinfo(title="Search Result", message=reply)
                i += 1
            # handle the address for download, which is get the download address from server
            elif word == "ADDRESS":
                # get filename and address
                filename = words[i]
                addr = (words[i + 1], int(words[i + 2]))
                i += 3
                # send download request
                msg = "DOWNLOAD " + filename
                self.socket.sendto(msg.encode(), addr)
            # handle the download request from other clients
            elif word == "DOWNLOAD":
                self.send_file(words[i], addr)
            # handle receiving file - get the file information
            elif word == "FILE":
                path = "download/" + words[i]
                self.fp_write = open(path, 'w')
                self.file_size = int(words[i + 1])
                i += 2
            # handle receiving file - end of the file download, check the file size
            elif word == "FILEEND":
                self.fp_write.close()
                size = os.path.getsize("download/" + words[i])
                if size == self.file_size:
                    msg = "File download success!\n" + "File size: " + str(size) + " B"
                    tk.messagebox.showinfo(title="File Download", message=msg)
                else:
                    msg = "Expect File size: " + str(self.file_size) + " B\n" + "File size: " + str(size) + " B\n"
                    msg += "Download fail!"
                    tk.messagebox.showinfo(title="File Download", message=msg)
            # handle receiving file - the first word don't match any keyword, this is the actual file data, write it
            # into the file
            elif i == 1:
                self.fp_write.write(data.decode())
                break;

    # receive message from server
    def receive(self):
        try:
            data, addr = self.socket.recvfrom(1024)
            if data:
                self.handle_receive(data, addr)
        except socket.timeout:
            pass
        # set up receiving data every 200ms
        self.after_id = self.current_window.after(200, self.receive)

    # run the gui
    def run(self):
        self.menu()


# run the client
if __name__ == '__main__':
    client = Client()
    client.run()
