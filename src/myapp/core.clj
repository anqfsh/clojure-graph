(ns myapp.core
  (:gen-class)
  (:require [clojure.set :as set]))

;; Example graph definition with weighted edges between vertices
(def G
  {:A {:B 1 :C 2}  ;; Vertex A connects to B with weight 1, and to C with weight 2
   :B {:D 4}       ;; Vertex B connects to D with weight 4
   :C {:D 2}       ;; Vertex C connects to D with weight 2
   :D {}})         ;; Vertex D has no outgoing edges


(defn generate-letter-vertices [N]
  (let [letters (mapv char (range 65 91))] ;; A-Z
    (mapv (fn [idx]
            (loop [i idx, name ""]
              (let [letter (nth letters (mod i 26))]
                (if (zero? (quot i 26))
                  (keyword (str letter name))
                  (recur (quot i 26) (str letter name))))))
          (range N))))


(defn random-directed-edge [vertices]
  (let [v1 (rand-nth vertices)
        v2 (rand-nth (remove #{v1} vertices))]
    [v1 v2]))


(defn make-graph [N S]
  ;; Create N vertices as :A, :B, etc.
  (let [vertices (generate-letter-vertices N)
        ;; Minimum spanning tree to ensure the graph is connected
        initial-edges (loop [connected #{(first vertices)}
                             remaining (rest vertices)
                             edge-list []]
                        (if (empty? remaining)
                          edge-list
                          (let [v1 (rand-nth (vec connected))
                                v2 (first remaining)]
                            (recur (conj connected v2)
                                   (rest remaining)
                                   (conj edge-list [v1 v2])))))
        ;; Add additional edges randomly until we reach S edges
        extra-edges (loop [edges initial-edges]
                      (if (>= (count edges) (min S (* N (dec N))))
                        edges
                        (let [new-edge (random-directed-edge vertices)]
                        ;;   (if (or (some #(= new-edge %) edges) ;; Avoid duplicate edges
                        ;;           (some #(= (reverse new-edge) %) edges)) ;; Avoid the reverse (A->B and B->A)
                          (if (some #(= new-edge %) edges) ;; Avoid duplicate edges
                            (recur edges)
                            (recur (conj edges new-edge))))))]
    ;; Convert edge list into the graph map structure with random weights
    (reduce (fn [graph [v1 v2]]
              (update graph v1 assoc v2 (inc (rand-int 10)))) ;; Random weights
            (into {} (map (fn [v] [v {}]) vertices))
            extra-edges)))


;; Dijkstra's Algorithm Implementation
(defn shortest-path
  [graph start end]
  (loop [distances {start 0}
         predecessors {}
         unvisited (set (keys graph))]
    (if (empty? unvisited)
      (let [path (loop [v end, path []]
                   (if (nil? v) path
                       (recur (predecessors v) (conj path v))))]
        ;; Handle non-existent vertices and cases where start and end vertices are the same
        ;; Returning nil simplifies calculations for eccentricity, radius, and diameter
        (if (or (empty? path) (and (= (count path) 1) (= (first path) end)))
          nil
          (rseq path)))
      (let [current (apply min-key (fn [v] (get distances v Double/POSITIVE_INFINITY)) unvisited)
            neighbors (graph current)]
        (recur
         (reduce (fn [d neighbor]
                   (let [alt (+ (get distances current Double/POSITIVE_INFINITY)
                                (get neighbors neighbor))]
                     (if (< alt (get d neighbor Double/POSITIVE_INFINITY))
                       (assoc d neighbor alt)
                       d)))
                 distances (keys neighbors))
         (reduce (fn [p neighbor]
                   (let [alt (+ (get distances current Double/POSITIVE_INFINITY)
                                (get neighbors neighbor))]
                     (if (< alt (get distances neighbor Double/POSITIVE_INFINITY))
                       (assoc p neighbor current)
                       p)))
                 predecessors (keys neighbors))
         (disj unvisited current))))))


(defn path-distance [graph path]
  (reduce (fn [acc [a b]]
            (+ acc (get-in graph [a b] 0))) ;; Sum edge weights
          0
          (partition 2 1 path))) ;; Partition path into pairs of consecutive vertices


(defn eccentricity [graph vertex]
  (let [vertices (disj (set (keys graph)) vertex) ;; Exclude the current vertex
        shortest-paths (map #(shortest-path graph vertex %) vertices)
        distances (map #(if % (path-distance graph %) Double/POSITIVE_INFINITY) shortest-paths)] ;; Return Double/POSITIVE_INFINITY if no path
    (apply max distances)))


(defn radius [graph]
  (let [eccentricities (map #(eccentricity graph %) (keys graph))]
    (apply min eccentricities)))


(defn diameter [graph]
  (let [eccentricities (map #(eccentricity graph %) (keys graph))]
    (apply max eccentricities)))


(def random-graph (make-graph 10 10))
(shortest-path random-graph (first (keys random-graph)) (last (keys random-graph))) ; => list of nodes which is the shortest path by edge weight between the 2 nodes, or no path if one does not exist.
(eccentricity random-graph (first (keys random-graph))) ; => number expressing eccentricity for `first` vertex in random-graph
(radius random-graph) ; => minimal eccentricity
(diameter random-graph) ; => maximal eccentricity


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
