import pddlpy

domprob = pddlpy.DomainProblem('domain.pddl', 'problem.pddl')

acao = 'putdown'
print("******************************************")
print("Estadoinicial")
print(domprob.initialstate())
print("******************************************")
print("Lista de operadores")
print(list(domprob.operators()))
print("******************************************")
print("Pre-condicoes")
print(len(list(domprob.ground_operator(acao))))

for elemento in list(domprob.ground_operator(acao)):
    print(elemento.precondition_pos)
print("******************************************")

for elemento in list(domprob.ground_operator(acao)):
    print(elemento.effect_pos)
print("******************************************")
