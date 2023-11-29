import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy {
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> path = new LinkedList<>();
        // Add start node to the open list and mark it as the current node
        Node current = new Node(start, 0, 0, 0, null);
        current.setH(start, end);
        current.setF(current.getG(), current.getH());
        Comparator<Node> n = Comparator.comparing(Node::getF);
        PriorityQueue<Node> open_list = new PriorityQueue<>(n);
        HashMap<Object, Object> O = new HashMap<>();
        HashMap<Object, Object> closed_list = new HashMap<>();
        open_list.add(current);

        while (!open_list.isEmpty()) {
            //Choose a node from the open list with the smallest f value and make it the current node
            current = open_list.poll();
            if (withinReach.test(current.getLoc(), end)) {
                while (current.getPrior() != null){
                    path.add(0, current.getLoc());
                    current = current.getPrior();
                }
                break;
            }
            //Analyze all valid adjacent nodes that are not on the closed list. For each valid neighbor:
            List<Point> neighbors = potentialNeighbors.apply(current.getLoc()).
                    filter(canPassThrough)
                    .collect(Collectors.toList());
            neighbors.removeIf(p -> closed_list.get(p) != null);

            for (Point p : neighbors) {
                int new_g = current.getG() + 1;
                // Determine distance from start node (g value)
                Node neigh = new Node(p, new_g, 0, 0, current);
                // If the calculated g value is better than a previously calculated g value,
                // replace the old g value with the new one and proceed to c.
                // Otherwise, skip to step a for the next node.
                if (O.containsKey(p)) {
                    Node existingNeighbor = (Node) O.get(p);
                    if (existingNeighbor.getG() < new_g) {
                        continue; // Skip this neighbor if the existing g-value is better
                    }
                    else {
                        open_list.remove(existingNeighbor); // Remove the node from the PriorityQueue
                        O.remove(p); // Remove the entry from the HashMap
                    }
                }

                neigh.setH(p, end);
                neigh.setF(neigh.getG(), neigh.getH());

                O.put(p, neigh);
                open_list.add(neigh);
            }
            // move current node to closed list
            closed_list.put(current.getLoc(), current);
        }
        return path;
    }
}
