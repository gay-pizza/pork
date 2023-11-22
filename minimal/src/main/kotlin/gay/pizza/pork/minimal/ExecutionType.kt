package gay.pizza.pork.minimal

import gay.pizza.pork.evaluator.EvaluatorProvider
import gay.pizza.pork.execution.ExecutionContextProvider
import gay.pizza.pork.frontend.World
import gay.pizza.pork.vm.VirtualMachineProvider

enum class ExecutionType(val id: String, val create: (World) -> ExecutionContextProvider) {
  Evaluator("evaluator", { world -> EvaluatorProvider(world) }),
  VirtualMachine("virtual-machine", { world -> VirtualMachineProvider(world) })
}
