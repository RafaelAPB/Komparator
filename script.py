from subprocess import Popen, CREATE_NEW_CONSOLE, PIPE
import sys
import time

#A12-Comparator
#    mediator
#    mediator-cli
#    (...)
#    script.py

code = """import sys
for line in sys.stdin: # poor man's `cat`
    sys.stdout.write(line)
    sys.stdout.flush()
""" 


def create_console():
    return Popen([sys.executable, "-c", code],
                  stdin=PIPE, bufsize=1, universal_newlines=True,
                  creationflags=CREATE_NEW_CONSOLE)

def println():
    print("\n\n")

supplier_count = eval(input("Number of suppliers to run (at least 2). Option: "))
compiling_console = create_console()


print("Compiling security module...")
security_process = Popen(['mvn', 'clean', 'install'], shell=True, cwd="./security", stdout=compiling_console.stdin)
security_process.wait()
print("Compiling supplier-ws module...")
supplier_ws_comp = Popen(['mvn', 'clean', 'install'], shell=True, cwd="./supplier-ws", stdout=compiling_console.stdin)
supplier_ws_comp.wait()


supplier_processes = []
println()
for i in range(1,supplier_count+1):
    supplier_console = create_console()
    print("Now running supplier"+str(i))
    supplier_processes += [Popen(['mvn', 'exec:java', '-Dws.i='+str(i)],
                                   shell=True, cwd="./supplier-ws", stdout=supplier_console.stdin)] 
println()
time.sleep(5)
print("Compiling supplier-ws-client module...")
supplier_client = Popen(['mvn', 'clean', 'install'], shell=True, cwd="./supplier-ws-cli", stdout=compiling_console.stdin)
supplier_client.wait()

print("Compiling mediator module...")
mediator = Popen(['mvn', 'clean', 'install'], shell=True, cwd="./mediator-ws", stdout=compiling_console.stdin)
mediator.wait()
println()

print("Now running mediator...")
mediator = Popen(['mvn', 'exec:java'], shell=True, cwd="./mediator-ws", stdout=create_console().stdin)
time.sleep(5)
print("Compiling mediator-cli module...")
mediator_cli = Popen(['mvn', 'clean', 'install'], shell=True, cwd="./mediator-ws-cli")
mediator_cli.wait()