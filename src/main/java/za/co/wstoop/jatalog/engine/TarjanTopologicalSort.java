package za.co.wstoop.jatalog.engine;

// Java program to find strongly connected
// components in a given directed graph
// using Tarjan's algorithm (single DFS)

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

public class TarjanTopologicalSort {

    // Driver code
    public static void main(String[] args) {
        {
            Graph2AndGraph2Refactored g1 = new Graph2AndGraph2Refactored();
            g1.calcBoth(5, Arrays.asList(
                    E.i(1, 0),
                    E.i(0, 2),
                    E.i(2, 1),
                    E.i(0, 3),
                    E.i(3, 4)));
        }
        {
            Graph2AndGraph2Refactored g2 = new Graph2AndGraph2Refactored();
            g2.calcBoth(4, Arrays.asList(
                    E.i(0, 1),
                    E.i(1, 2),
                    E.i(2, 3)));
        }
        {
            Graph2AndGraph2Refactored g3 = new Graph2AndGraph2Refactored();
            g3.calcBoth(7, Arrays.asList(
                    E.i(0, 1),
                    E.i(1, 2),
                    E.i(2, 0),
                    E.i(1, 3),
                    E.i(1, 4),
                    E.i(1, 6),
                    E.i(3, 5),
                    E.i(4, 5)));
        }
        {
            Graph2AndGraph2Refactored g4 = new Graph2AndGraph2Refactored();
            g4.calcBoth(11, Arrays.asList(
                    E.i(0, 1),
                    E.i(0, 3),
                    E.i(1, 2),
                    E.i(1, 4),
                    E.i(2, 0),
                    E.i(2, 6),
                    E.i(3, 2),
                    E.i(4, 5),
                    E.i(4, 6),
                    E.i(5, 6),
                    E.i(5, 7),
                    E.i(5, 8),
                    E.i(5, 9),
                    E.i(6, 4),
                    E.i(7, 9),
                    E.i(8, 9),
                    E.i(9, 8)));
        }
        {
            Graph2AndGraph2Refactored g5 = new Graph2AndGraph2Refactored();
            g5.calcBoth(5, Arrays.asList(
                    E.i(0, 1),
                    E.i(1, 2),
                    E.i(2, 3),
                    E.i(2, 4),
                    E.i(3, 0),
                    E.i(4, 2)));
        }
        {
            Graph2AndGraph2Refactored g6 = new Graph2AndGraph2Refactored();
            g6.calcBoth(8, Arrays.asList(
                    E.i(0, 1),
                    E.i(1, 2),
                    E.i(1, 7),
                    E.i(2, 3),
                    E.i(2, 6),
                    E.i(3, 4),
                    E.i(4, 2),
                    E.i(4, 5),
                    E.i(6, 3),
                    E.i(6, 5),
                    E.i(7, 0),
                    E.i(7, 6)));
        }
        {
            Graph2AndGraph2Refactored g6 = new Graph2AndGraph2Refactored();
            g6.calcBoth(11, Arrays.asList(
                    E.i(0, 1),
                    E.i(0, 2),
                    E.i(1, 3),
                    E.i(1, 4),
                    E.i(1, 5),
                    E.i(1, 6),
                    E.i(2, 7),
                    E.i(2, 8),
                    E.i(2, 9),
                    E.i(2, 10)));
        }
    }

    public static class Graph2AndGraph2Refactored {
        void calcBoth(int aSize, List<E> edges) {
            {
                Graph2 g2 = new Graph2(aSize);
                edges.forEach(g2::addEdge);
                g2.calcSCC();
            }
            {
                Graph2Refactored1 g2r1 = new Graph2Refactored1();
                edges.forEach(g2r1::addEdge);
                g2r1.calcSCC();
            }
        }
    }


    // from https://rosettacode.org/wiki/Tarjan#Java
    public static class Graph2 {
        private final List<Set<Integer>> adjacencyLists;
        private final List<Integer> vertices = new ArrayList<>();
        private final Stack<Integer> stack = new Stack<>();
        private final Map<Integer, Integer> numbers = new HashMap<>();
        private final Map<Integer, Integer> lowlinks = new HashMap<>();
        private final List<Set<Integer>> stronglyConnectedComponents = new ArrayList<>();
        private int index = 0;

        public Graph2(int aSize) {
            adjacencyLists = new ArrayList<>(aSize);
            for (int i = 0; i < aSize; i++) {
                vertices.add(i);
                adjacencyLists.add(new HashSet<>());
            }
        }

        public void addEdge(int aFrom, int aTo) {
            adjacencyLists.get(aFrom).add(aTo);
        }

        public void addEdge(E e) {
            addEdge(e.f, e.t);
        }

        public void calcSCC() {
            System.out.printf("%nGraph2%n" +
                            "adjacencyLists: '%s', vertices '%s',%n" +
                            "scc: '%s',%n" +
                            "numbers: %s,%n" +
                            "lowlinks: '%s'%n",
                    adjacencyLists, vertices,
                    getSCC(),
                    numbers,
                    lowlinks);
        }

        public List<Set<Integer>> getSCC() {
            index = 0;
            for (int vertex : vertices) {
                if (!numbers.containsKey(vertex)) {
                    stronglyConnect(vertex);
                }
            }
            return stronglyConnectedComponents;
        }

        private void stronglyConnect(int aVertex) {
            numbers.put(aVertex, index);
            lowlinks.put(aVertex, index);
            index += 1;
            stack.push(aVertex);

            for (int adjacent : adjacencyLists.get(aVertex)) {
                if (!numbers.containsKey(adjacent)) {
                    stronglyConnect(adjacent);
                    lowlinks.put(aVertex, Math.min(lowlinks.get(aVertex), lowlinks.get(adjacent)));
                } else if (stack.contains(adjacent)) {
                    lowlinks.put(aVertex, Math.min(lowlinks.get(aVertex), numbers.get(adjacent)));
                }
            }

            if (lowlinks.get(aVertex).equals(numbers.get(aVertex))) {
                Set<Integer> stronglyConnectedComponent = new HashSet<>();
                int top;
                do {
                    top = stack.pop();
                    stronglyConnectedComponent.add(top);
                } while (top != aVertex);

                stronglyConnectedComponents.add(stronglyConnectedComponent);
            }
        }

    }


    // from https://rosettacode.org/wiki/Tarjan#Java
    public static class Graph2Refactored1 {
        //private List<Set<Integer>> adjacencyLists;
        private final Map<Node, List<Node>> adjacencyLists = new HashMap<>();
        private final Stack<Node> stack = new Stack<>();
        private final Map<Node, Integer> numbers = new HashMap<>();
        private final Map<Node, Integer> lowlinks = new HashMap<>();
        private final List<List<Node>> stronglyConnectedComponents = new ArrayList<>();
        //private List<Integer> vertices = new ArrayList<>();
        private int index = 0;

        public void addEdge(Node aFrom, Node aTo) {
            List<Node> aToSet = adjacencyLists.get(aFrom);
            if (aToSet == null) {
                aToSet = new ArrayList<>();
                adjacencyLists.put(aFrom, aToSet);
            }
            aToSet.add(aTo);
        }

        public void addEdge(E e) {
            Node aFrom = Node.i(e.f);
            Node aTo = Node.i(e.t);
            addEdge(aFrom, aTo);
        }


        public void calcSCC() {
            System.out.printf("%nGraph2Refactored1 in seventh graph%n" +
                            "adjacencyLists: '%s'%n" +
                            "scc: '%s',%n" +
                            "numbers: %s,%n" +
                            "lowlinks: '%s'%n",
                    adjacencyLists,
                    getSCC(), numbers,
                    lowlinks);
        }

        public List<List<Node>> getSCC() {
            index = 0;
            for (Node vertex : adjacencyLists.keySet()) {
                if (!numbers.containsKey(vertex)) {
                    stronglyConnect(vertex);
                }
            }
            return stronglyConnectedComponents;
        }

        private void stronglyConnect(Node aVertex) {
            numbers.put(aVertex, index);
            lowlinks.put(aVertex, index);
            index += 1;
            stack.push(aVertex);

            int mode = 0;
            if (mode == 0) {
                Consumer<List<Node>> c = nodeSet -> {
                    for (Node adjacent : nodeSet) {
                        if (!numbers.containsKey(adjacent)) {
                            stronglyConnect(adjacent);
                            lowlinks.put(aVertex, Math.min(lowlinks.get(aVertex), lowlinks.get(adjacent)));
                        } else if (stack.contains(adjacent)) {
                            lowlinks.put(aVertex, Math.min(lowlinks.get(aVertex), numbers.get(adjacent)));
                        }
                    }
                };
                List<Node> value = adjacencyLists.get(aVertex);
                if (value != null) c.accept(value);
            } else {
                if (adjacencyLists.containsKey(aVertex)) {
                    for (Node adjacent : adjacencyLists.get(aVertex)) {
                        if (!numbers.containsKey(adjacent)) {
                            stronglyConnect(adjacent);
                            lowlinks.put(aVertex, Math.min(lowlinks.get(aVertex), lowlinks.get(adjacent)));
                        } else if (stack.contains(adjacent)) {
                            lowlinks.put(aVertex, Math.min(lowlinks.get(aVertex), numbers.get(adjacent)));
                        }
                    }
                } else {
                    //System.out.printf("? bad aVertex '%s'", aVertex);
                }
            }

            if (lowlinks.get(aVertex).equals(numbers.get(aVertex))) {
                List<Node> aStronglyConnectedComponent = new ArrayList<>();
                Node top;
                do {
                    top = stack.pop();
                    aStronglyConnectedComponent.add(top);
                } while (top != aVertex);

                stronglyConnectedComponents.add(aStronglyConnectedComponent);
            }
        }


    }

    static class E {
        int f, t;

        E(int f, int t) {
            this.f = f;
            this.t = t;
        }

        static E i(int f, int t) {
            return new E(f, t);
        }
    }

    static class Node extends NodeT<String> {
        public Node(String name) {
            super(name);
        }

        public static Node i(int n) {
            return new Node("n" + n);
        }
    }

    static class NodeT<T> {

        T name;

        public NodeT(T name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeT node = (NodeT) o;
            return Objects.equals(name, node.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            //return "Node{"+"name='" + name + '\'' +'}';
            return "" + name;
        }
    }
}
