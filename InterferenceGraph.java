import java.io.IOException;
import java.util.*;

public class InterferenceGraph {
    private HashSet<InterferenceGraphNode> nodes;
    private HashSet<InterferenceGraphNode> visibleNodes;
    private CFGGenerator generator;
    private HashSet<Instruction> loopInstrs;
    private HashMap<String, HashSet<LiveRange>> webs;
    
    private static String[] intList, floatList;

    private static final int LOOP_COST = 10;

    private static final String[] floatRegArr = {"$f20", "$f21", "$f22", "$f23", "$f24", "$f25", "$f26", "$f27", "$f28", "$f29", "$f30", "$f31"};
    private static final String[] intRegArr = {"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"};
    
    public InterferenceGraph(CFGGenerator generator, BasicBlock graphRoot) {
        this.generator = generator;
        this.nodes = new HashSet<InterferenceGraphNode>();
        this.visibleNodes = new HashSet<InterferenceGraphNode>();
        InterferenceGraph.intList = graphRoot.ints;
        InterferenceGraph.floatList = graphRoot.floats;
        this.loopInstrs = new HashSet<Instruction>();
        

        for (BasicBlock block: this.generator.getLoopBlocks()) {
            this.loopInstrs.addAll(block.getLines());
        }

        HashMap<String, HashSet<LiveRange>> webs = this.generator.createLiveRanges(graphRoot);

        int count = 0;
        for (HashSet<LiveRange> set : webs.values()) {
            for (LiveRange range : set) {
                InterferenceGraphNode node = new InterferenceGraphNode(range.getVarName() + " " +Integer.toString(count++), range.instructions);
                this.nodes.add(node);
                this.visibleNodes.add(node);
            }
        }

        HashSet<Instruction> temp = new HashSet<Instruction>();

        for (InterferenceGraphNode node1 : nodes) {
            for (InterferenceGraphNode node2 : nodes) {
                if (node1.getName().split(" ")[0].equals(node2.getName().split(" ")[0])) continue; // variable can't interfere with itself
                if (node1.isInt != node2.isInt) continue; //ints can't interfere with floats
                
                temp.clear();
                temp.addAll(node1.lines);
                temp.removeAll(node2.lines);

                if (temp.size() != node1.lines.size()) {
                    node1.addEdge(node2);
                }
            }
        }

    }

    public void color() {
        Stack<InterferenceGraphNode> nodeStack = new Stack<>();
        HashSet<InterferenceGraphNode> nodeCopy = new HashSet<>();
        HashSet<InterferenceGraphNode> spillNodes = new HashSet<>();
        
        boolean changed;
        while (nodeStack.size() < this.nodes.size()) {
            do {
                // System.out.println("looping!");
                changed = false;
                nodeCopy.clear();
                nodeCopy.addAll(this.visibleNodes);
                // System.out.println("!" + this.nodes);
                for (InterferenceGraphNode node : nodeCopy) {
                    // System.out.println(node.edges);
                    if ((node.isInt && node.getVisibleEdges().size() < intRegArr.length) 
                        || (!node.isInt && node.getVisibleEdges().size() < floatRegArr.length)) {
                        changed = true;
                        this.hideNode(node);
                        nodeStack.push(node);
                    }
                }
            } while (changed);
            if (nodeStack.size() != this.nodes.size()) {
                int minCost = Integer.MAX_VALUE;
                int temp;
                InterferenceGraphNode minCostNode = null;

                for (InterferenceGraphNode node : this.visibleNodes) {
                    temp = calcCost(node);
                    if (temp < minCost) {
                        minCost = temp;
                        minCostNode = node;
                    }
                }

                this.hideNode(minCostNode);
                spillNodes.add(minCostNode);
                nodeStack.push(minCostNode);
            }
        }

        int i;

        while (!nodeStack.isEmpty()) {
            InterferenceGraphNode currentNode = nodeStack.pop();
            currentNode.updateAdjColors();
            if (spillNodes.contains(currentNode)) {
                currentNode.setColor("SPILL");
            } else if (currentNode.isInt) {
                for (i = 0; i < InterferenceGraph.intRegArr.length; i++) {
                    // System.out.println(currentNode.adjColors);
                    if (!currentNode.adjColors.contains(InterferenceGraph.intRegArr[i])) {
                        currentNode.setColor(InterferenceGraph.intRegArr[i]);
                        break;
                    }
                }
            } else {
                for (i = 0; i < InterferenceGraph.floatRegArr.length; i++) {
                    if (!currentNode.adjColors.contains(InterferenceGraph.floatRegArr[i])) {
                        currentNode.setColor(InterferenceGraph.floatRegArr[i]);
                    }
                }
            }
        }

        // for (InterferenceGraphNode node : this.nodes) {
        //     System.out.println(String.format("%-8s:%-5s %s", node.name, node.color, node.edges.toString()));
        // }

    }

    public LinkedHashMap<Instruction, HashMap<String, String>> generateRegisterMap() {
        LinkedHashMap<Instruction, HashMap<String, String>> map = new LinkedHashMap<>();
        HashMap<Integer, Instruction> tempOrdering = new HashMap<>();

        for (InterferenceGraphNode node : this.nodes) {
            for (Instruction instr : node.lines) {
                tempOrdering.put(instr.absoluteNumber, instr);
            }
        }

        for (int i = 0; i < Instruction.getLineCounter(); i++) {
            if (tempOrdering.keySet().contains(i)) {
                map.put(tempOrdering.get(i), new HashMap<String, String>());
            }
        }

        
        for (InterferenceGraphNode node : this.nodes) {
            for (Instruction instr : node.lines) {
                map.get(instr).put(node.varname, node.color);
            }
        }
        return map;
    }

    private int calcCost(InterferenceGraphNode node) {
        HashSet<Instruction> temp = new HashSet<>();
        temp.addAll(node.lines);
        temp.removeAll(this.loopInstrs);

        return temp.size() + InterferenceGraph.LOOP_COST * (node.lines.size() - temp.size());

    }

    private void removeNode(InterferenceGraphNode node) {
        this.nodes.remove(node);
        this.visibleNodes.remove(node);
        for (InterferenceGraphNode edge : node.edges) {
            edge.removeEdge(node);
            edge.hideEdge(node);
        }
    }

    private void hideNode(InterferenceGraphNode node) {
        this.visibleNodes.remove(node);
        for (InterferenceGraphNode visEdge : node.visibleEdges) {
            visEdge.hideEdge(node);
        }
    }

    public void printGraph() {
        for (InterferenceGraphNode node : this.nodes) {
            System.out.println(node.getName() + ": " + node.getEdges().toString());
        }
    }

    public void printNodes() {
        for (InterferenceGraphNode node : this.nodes) {
            System.out.println(node.getName() + ":");
            for (Instruction line : node.lines) {
                System.out.println("\t" + line);
            }
        }
    }

    private class InterferenceGraphNode {
        private String name;
        private String varname;
        private HashSet<Instruction> lines;
        private boolean isInt;
        private String color;
        private HashSet<InterferenceGraphNode> edges;
        private HashSet<InterferenceGraphNode> visibleEdges;
        private HashSet<String> adjColors;

        public InterferenceGraphNode(String name, HashSet<Instruction> lines) {
            this.name = name;
            this.lines = lines;
            this.color = null;
            this.edges = new HashSet<InterferenceGraphNode>();
            this.visibleEdges = new HashSet<InterferenceGraphNode>();
            this.adjColors = new HashSet<String>();
            this.isInt = false;

            this.varname = this.name.split(" ")[0];

            for (String s : InterferenceGraph.intList) {
                if (s.equals(this.varname)) {
                    this.isInt = true;
                    break;
                }
            }
        }

        public void addEdge(InterferenceGraphNode node) {
            this.edges.add(node);
            this.visibleEdges.add(node);
            if (node.color != null) this.adjColors.add(node.color);
        }

        public void removeEdge(InterferenceGraphNode node) {
            this.visibleEdges.remove(node);
            boolean removed = this.edges.remove(node);
            // if (removed) {
            //     System.out.println("Removing " + node);
            // }
            if (node.color != null && removed) this.adjColors.remove(node.color);
        }

        public void hideEdge(InterferenceGraphNode node) {
            this.visibleEdges.remove(node);
        }

        public void updateAdjColors() {
            for (InterferenceGraphNode node : this.edges) {
                if (node.color != null) this.adjColors.add(node.color);
            }
        }

        public void setColor(String color) {
            this.color = color;
        }

        public HashSet<InterferenceGraphNode> getEdges() {
            return this.edges;
        }

        public HashSet<InterferenceGraphNode> getVisibleEdges() {
            return this.visibleEdges;
        }

        public String getColor() {
            return this.color;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name.split(" ")[1];
        }
    }
}