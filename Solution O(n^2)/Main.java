import java.util.*;

public class Main {
    public class Node {
        private int id;
        private int fuelCost;
        private Node parent;
        private List<Node> children = new LinkedList<>();
        private int distanceFromParent;

        // Lowest cost required to travel from root to node.
        private long smallestArrivalCost;

        public Node(int id, int distance, int fuelCost, Node parent) {
            this.id = id;
            this.fuelCost = fuelCost;
            this.parent = parent;
            this.distanceFromParent = distance;
            this.smallestArrivalCost = -1;
        }

        public boolean addChild(Node node) {
            return this.children.add(node);
        }

        @Override
        public String toString() {
            return String.valueOf(this.id);
        }
    }

    public class Traverser {

        // Source node
        private Node node;

        // Array used to keep track of whether a node is
        // already previously visited in the traversal.
        private boolean[] been = new boolean[Main.this.n];

        public Traverser(Node node) {
            this.node = node;
            this.traverse(node, 0);
        }

        // Update samllestArrivalCost of every node if going
        // through source node using DFS. Running time: O(n)
        public void traverse(Node curr, long distance) {
            if (been[curr.id]) return;

            this.been[curr.id] = true;

            // Cost to travel from source node to current
            // node using fuel price of source node.
            long costThrough = this.node.smallestArrivalCost + this.node.fuelCost * distance;

            if (curr.smallestArrivalCost == -1) {
                curr.smallestArrivalCost = costThrough;
            } else {
                // Update if going through source node is cheaper.
                curr.smallestArrivalCost = Math.min(curr.smallestArrivalCost, costThrough);
            }

            // Recurse on parent and children.
            if (curr.parent != null) {
                traverse(curr.parent, distance + curr.distanceFromParent);
            }
            for (Node n : curr.children) {
                traverse(n, distance + n.distanceFromParent);
            }
        }
    }
    private int n;

    private int[] cost;

    private int[] parent;

    private int[] distance;

    public Main() {
        Scanner scan = new Scanner(System.in);
        this.n = scan.nextInt();

        this.cost = new int[this.n];
        for (int i = 0; i < this.n; i++) {
            this.cost[i] = scan.nextInt();
        }

        this.parent = new int[this.n - 1];
        this.distance = new int[this.n - 1];
        for(int i = 0; i < this.n - 1; i++) {
            this.parent[i] = scan.nextInt();
            this.distance[i] = scan.nextInt();
        }
    }

    public long findLowestCost() {
        List<Node> nodes = new ArrayList<>();

        // PriorityQueue sorts nodes by decreasing fuel costs.
        PriorityQueue<Node> sortedByCost = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return n2.fuelCost - n1.fuelCost;
            }
        });

        Node root = new Node(0, 0, this.cost[0], null);
        root.smallestArrivalCost = 0;
        nodes.add(root);
        sortedByCost.add(root);

        // Construct Tree, takes O(nlogn)
        for (int i = 0; i < this.n - 2; i++) {
            Node node = new Node(i + 1, this.distance[i], this.cost[i + 1], nodes.get(this.parent[i] - 1));
            nodes.get(this.parent[i] - 1).addChild(node);
            nodes.add(i + 1, node);
            if (node.fuelCost < root.fuelCost) {
                // Appending into PQ takes O(logn)
                sortedByCost.add(node);
            }
        }

        Node target = new Node(this.n - 1, this.distance[this.n - 2],
                             this.cost[this.n - 1], nodes.get(this.parent[this.n - 2] - 1));
        nodes.get(this.parent[this.n - 2] -1).addChild(target);

        // Traversal from every node. Total run time: O(n^2)
        while (!sortedByCost.isEmpty()) {
            Traverser traverser = new Traverser(sortedByCost.poll());
        }

        return target.smallestArrivalCost;
    }

    public static void main(String[] args) {
        Main main = new Main();
        System.out.println(main.findLowestCost());
    }
}