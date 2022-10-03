from tkinter import *
import threading
from subprocess import Popen, PIPE, STDOUT
import sys
import time

CREATE_NO_WINDOW = 0x08000000

class Othello:
    # turn == 1 is black turn == -1 is white
    def __init__(self, boardsize = 8):
        self.boardsize = boardsize
        self.turn = 1
        self.gui_grid = [[]]*self.boardsize
        self.grid = [[]]*self.boardsize
        for i in range(self.boardsize):
            self.grid[i] = [0]*self.boardsize
            self.gui_grid[i] = [0]*self.boardsize
        self.grid[3][3] = -1
        self.grid[3][4] = 1
        self.grid[4][3] = 1
        self.grid[4][4] = -1


    def gui_grid_clear(self):
        for i in range(len(self.gui_grid)):
            for j in range(len(self.gui_grid[i])):
                self.gui_grid[i][j] = 0

    
    def __str__(self):
        # x is black o is white
        output = '  '
        for i in range(self.boardsize):
            output += str(i) + ' '
        output += '\n'
        for i in range(self.boardsize):
            output += str(i) + ' '
            for j in range(self.boardsize):
                c = '.'
                if self.grid[i][j] == 1:
                    c = 'x'
                elif self.grid[i][j] == -1:
                    c = 'o'
                output += c + ' '
            output += '\n'
        return output

    def check_move(self, move, player = None):
        if player == None:
            player = self.turn
            
        # move = (a, b). The co-ordinates of the move
        # player = 1 if black is moving, or -1 if white is moving.
        if self.grid[move[0]][move[1]] != 0:
            return False
        d = [[0, 1], [1, 0], [0, -1], [-1, 0], [1, 1], [-1, 1], [1, -1], [-1, -1]]
        for i in d:
            a, b = move[0] + i[0], move[1] + i[1]
            flag = False
            while 0 <= a < self.boardsize and 0 <= b < self.boardsize:
                if not flag and self.grid[a][b] == player:
                        break
                if self.grid[a][b] == -player:
                    flag = True
                elif self.grid[a][b] == 0:
                    break
                elif flag:
                    return True
                a += i[0]
                b += i[1]


    def find_all_moves(self, player = None):
        if player == None:
            player = self.turn
            
        # If no moves then (-1, -1) is outputted as a move
        moves = []
        for i in range(self.boardsize):
            for j in range(self.boardsize):
                if self.check_move((i, j), player):
                    moves.append((i, j))
        if len(moves) == 0:
            moves.append((-1, -1))
        return moves


    def play_move(self, move, player = None):
        if player == None:
            player = self.turn
            
        possible_moves = self.find_all_moves(player)
        if move in possible_moves:
            if move == (-1, -1):
                self.turn = -player
                return True
            self.gui_grid_clear()
            self.grid[move[0]][move[1]] = player
            self.gui_grid[move[0]][move[1]] = 1
            d = [[0, 1], [1, 0], [0, -1], [-1, 0], [1, 1], [-1, 1], [1, -1], [-1, -1]]
            for i in d:
                a, b = move[0] + i[0], move[1] + i[1]
                flag = False
                while 0 <= a < self.boardsize and 0 <= b < self.boardsize:
                    if not flag and self.grid[a][b] == player:
                        break
                    elif self.grid[a][b] == -player:
                        flag = True
                    elif self.grid[a][b] == 0:
                        break
                    elif flag:
                        self.gui_grid[a][b] = 3
                        while (a, b) != move:
                            a -= i[0]
                            b -= i[1]
                            self.gui_grid[a][b] = 2
                            self.grid[a][b] = player
                        self.gui_grid[a][b] = 1
                        break
                    a += i[0]
                    b += i[1]

            self.turn = -player
            return True
        return False

    def game_over(self):
        # None: game not over
        # 1: black wins
        # -1: white wins
        # 0: draw
        if (self.find_all_moves(1) == [(-1, -1)] and
            self.find_all_moves(-1) == [(-1, -1)]):
            total = 0
            for i in range(self.boardsize):
                for j in range(self.boardsize):
                    total += self.grid[i][j]
            if total > 0:
                return 1
            elif total < 0:
                return -1
            else:
                return 0
        return None
    

class Gui(threading.Thread):
    F = 504
    d = F//8
    def __init__(self, game, name1, name2):
        self.name1 = name1
        self.name2 = name2
        self.game = game
        threading.Thread.__init__(self)
        self.start()

    def callback(self):
        self.root.quit()

    def run(self):
        self.root = Tk()
        self.root.protocol("WM_DELETE_WINDOW", self.callback)
        self.canvas = Canvas(self.root, bg="dark green", height=504,width=504)


        for i in range(7):
            self.canvas.create_line(Gui.d*i + Gui.d, 0, Gui.d*i + Gui.d, Gui.F, tag='bac')
        for i in range(7):
            self.canvas.create_line(0, Gui.d*i + Gui.d, Gui.F, Gui.d*i + Gui.d, tag='bac')
        s = 0.45    
        self.canvas.create_oval(Gui.d*(1.5+s), Gui.d*(1.5+s), Gui.d*(2.5-s), Gui.d*(2.5-s), fill ='black', tag='bac')
        self.canvas.create_oval(Gui.d*(5.5+s), Gui.d*(1.5+s), Gui.d*(6.5-s), Gui.d*(2.5-s), fill ='black', tag='bac')
        self.canvas.create_oval(Gui.d*(1.5+s), Gui.d*(5.5+s), Gui.d*(2.5-s), Gui.d*(6.5-s), fill ='black', tag='bac')
        self.canvas.create_oval(Gui.d*(5.5+s), Gui.d*(5.5+s), Gui.d*(6.5-s), Gui.d*(6.5-s), fill ='black', tag='bac')
        label = Label(self.root, text=self.name1 + " vs " +  self.name2)
        label.pack()
        self.canvas.pack()
        self.root.after(100, self.check)
        self.root.mainloop()

        
    def check(self):
        of = 2
        self.canvas.delete('for')
        for i in range(len(self.game.grid)):
            for j in range(len(self.game.grid[i])):
                if self.game.gui_grid[j][i] == 1:
                    self.canvas.create_rectangle(Gui.d*i, Gui.d*j, Gui.d*(i + 1), Gui.d*(j + 1), tag= 'for', fill= 'green')
                    self.canvas.create_rectangle(Gui.d*i, Gui.d*j, Gui.d*(i + 1), Gui.d*(j + 1),width=1, tag= 'for', outline= 'gold')
                if self.game.gui_grid[j][i] == 2:
                    self.canvas.create_rectangle(Gui.d*i, Gui.d*j, Gui.d*(i + 1), Gui.d*(j + 1), tag= 'for', fill= 'green')
                    #self.canvas.create_rectangle(Gui.d*i, Gui.d*j, Gui.d*(i + 1), Gui.d*(j + 1),width=1, tag= 'for', outline= 'red')
                if self.game.gui_grid[j][i] == 3:
                    self.canvas.create_rectangle(Gui.d*i, Gui.d*j, Gui.d*(i + 1), Gui.d*(j + 1), tag= 'for', fill= 'green')
                    #self.canvas.create_rectangle(Gui.d*i, Gui.d*j, Gui.d*(i + 1), Gui.d*(j + 1),width=1 ,tag= 'for', outline= 'light goldenrod')


        for i in range(len(self.game.grid)):
            for j in range(len(self.game.grid[i])):
                if self.game.grid[j][i] == 1:
                    self.canvas.create_oval(Gui.d*i + of, Gui.d*j + of, Gui.d*(i + 1) - of,
                                            Gui.d*(j + 1) - of, tag='for',fill='black')
                elif self.game.grid[j][i] == -1:
                    self.canvas.create_oval(Gui.d*i + of, Gui.d*j + of, Gui.d*(i + 1) - of,
                                            Gui.d*(j + 1) - of, tag='for',fill='white')
        s = 0.45 
        self.canvas.create_oval(Gui.d*(1.5+s), Gui.d*(1.5+s), Gui.d*(2.5-s), Gui.d*(2.5-s), fill ='black', tag='for')
        self.canvas.create_oval(Gui.d*(5.5+s), Gui.d*(1.5+s), Gui.d*(6.5-s), Gui.d*(2.5-s), fill ='black', tag='for')
        self.canvas.create_oval(Gui.d*(1.5+s), Gui.d*(5.5+s), Gui.d*(2.5-s), Gui.d*(6.5-s), fill ='black', tag='for')
        self.canvas.create_oval(Gui.d*(5.5+s), Gui.d*(5.5+s), Gui.d*(6.5-s), Gui.d*(6.5-s), fill ='black', tag='for')
        self.root.after(50, self.check)

    

def output_input(channel, out, required_output = True):
    if channel == None and required_output:
        return input()
    elif channel == None:
        return None
    else:
        #received = channel.communicate(input= bytes(out + "\n", 'utf-8'), timeout=2000)
        #return received[0]
        channel.stdin.write(bytes(out + '\n', 'utf-8'))
        channel.stdin.flush()
        if required_output:
            return channel.stdout.readline()
    return None

def to_brackets(s, output = True):
    # 'a b' to (a, b)
    if output:
        print(s)
    #print(s)
    s = s.split()
    return (int(s[0]), int(s[1]))

def from_brackets(s):
    # (a, b) to 'a b'
    return str(s[0]) + ' ' + str(s[1])

def gameloop(game, input_rules, output = True):
    INPUT = []
    for i in range(2):
        if len(input_rules[i]) > 1:
            isCpp = True
            if len(input_rules[i][0]) > 1:
                #non c++
                isCpp = False
            INPUT.append(Popen(input_rules[i][0], cwd=input_rules[i][1],
                            creationflags=CREATE_NO_WINDOW, shell = isCpp,
                                stdout= PIPE, stdin= PIPE, stderr= STDOUT))
        else:
            INPUT.append(None)
    if output:
        print(game)
    current_move = output_input(INPUT[0], '1')
    output_input(INPUT[1], '-1', required_output = False)
    
    while game.game_over() == None:
        time.sleep(0.1)
        if game.turn == 1:
            if output:
                print("Player x's turn'")
        else:
            if output:
                print("Player o's turn")
        if output:
            print("Input move")
        if INPUT[(-1*game.turn + 1)//2] != None:
            current_move = current_move.decode("utf-8")
        if current_move == None:
            current_move = '-1 -1'
        current_move = to_brackets(current_move, output= output)
        if not game.play_move(current_move):
            if output:
                print("Invalid move: ", current_move)
            return -game.turn

        ind = 1
        if game.turn == 1:
            ind = 0
        if output:
            print(game)
        current_move = output_input(INPUT[ind], from_brackets(current_move))
    for i in INPUT:
        if i != None:
            i.kill()
    return game.game_over()


def play_robots(AIs, GUI = True, kill_gui = False, output = True):
    board = Othello()
    if GUI:
        gui = Gui(board, AIs[0][-1], AIs[1][-1])
    result = gameloop(board, AIs, output= output)
    if kill_gui:
        gui.callback()
    return result

def interpret_result(result):
    if result == 1:
        print('x wins')
    elif result == -1:
        print('o wins')
    else:
        print('draw')

def score_robots(AIs):
    first = [0.0, 0.0]
    second = [0.0, 0.0]
    M = 30
    for i in range(M//2):
        x = play_robots(AIs, GUI = False, output= False)
        if x == 1:
            first[0] += 1
        elif x == -1:
            second[1] += 1
        else:
            first[0] += 0.5
            second[1] += 0.5
        print(str(i + 1) + "/" + str(M))
    AIs[0], AIs[1] = AIs[1], AIs[0]
    
    for i in range(M//2):
        x = play_robots(AIs, GUI = False, output= False)
        if x == 1:
            second[0] += 1
        elif x == -1:
            first[1] += 1
        else:
            second[0] += 0.5
            first[1] += 0.5
        print(str(i + 1 + M//2) + "/" + str(M))
    AIs[0], AIs[1] = AIs[1], AIs[0]
    print(AIs[0][-1], 'black:', first[0], 'white:', first[1])
    print(AIs[1][-1], 'black:', second[0], 'white:', second[1])
if __name__ == '__main__':

    KEYBOARD = ["Human"]
    RANDOM_AI = [["python", "rando_ai.py"], "Random", "Random bot"]
    TAARIQ = [["java", "-cp", ".;encog-core-3.4.jar", "TaariqLearning", "Freya"], "Taariq_java", "Taariq(Freya)"]

    '''
    This was for an AI competition between friends.
    Below are the commands to run their AIs.
    However their code has not been included in the github repo.
    '''
    
    #EMILE = [["python", "minmax.py"], "Emile", "Emile"]
    #AARON = [["Aaron"], "Aaron", "Aaron"]
    #ADRI = [["adri_ai"], "AdriOthelloAI", "Adri"]
    print(play_robots([TAARIQ, TAARIQ]))
    #interpret_result(play_robots([AARON, RANDOM_AI]))
    print("Done")
    #score_robots([TAARIQ, FREYA])
