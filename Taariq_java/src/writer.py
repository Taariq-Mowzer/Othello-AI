import random
files = ["Epoch6","Epoch7", "Last"]

merge = "GenesisData"
f = []
for i in files:
    f.append(open("EpochData/" + i + ".txt", 'r'))
try:
    final = open("TrainingData/" + merge + ".txt", 'x')
except:
    final = open("TrainingData/" + merge + ".txt", 'w')

tmp = []

for i in f:
    for j in i:
        tmp.append(j.rstrip().split())

M = {}

def proc(lip):
    global M
    if (len(lip) != 131):
        print("Mistake")
        return
    if (random.randint(0, 1) == 1):
        for i in range(64):
            lip[i], lip[i + 64] = lip[i + 64], lip[i]
        lip[128], lip[129] = lip[129], lip[128]
        lip[130] = 1.0 - float(lip[130])
    x = float(lip.pop())
    for i in range(len(lip)):
        lip[i] = str(lip[i])
        
    lip = " ".join(lip)
    if lip in M:
        tmp = M[lip]
        M[lip] = [tmp[0] + x, tmp[1] + 1]
    else:
        M[lip] = [x, 1]   


for i in range(len(tmp)):
    proc(tmp[i])

tmp = []
avg = 0
n = 0
for i in M:
    tmp.append(i + " " + str(M[i][0]/M[i][1]))
    n += 1
    avg += M[i][1]
print(avg - len(tmp))
random.shuffle(tmp)
for i in tmp:
    final.write(i)
    final.write("\n")
final.close()

for i in f:
    i.close()
