package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private Graph<ArtObject, DefaultWeightedEdge> graph;
	private List<ArtObject> oggetti;
	private ArtsmiaDAO dao;
	private Map<Integer, ArtObject> idMap;
	
	public Model() {
		
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.oggetti = new ArrayList<ArtObject>();
		this.dao= new ArtsmiaDAO();
		this.idMap = new HashMap<Integer, ArtObject>();
	}
	
	private void loadNodes() {
		
		//creo lista tutti oggetti
		if(this.oggetti.isEmpty()) {
			this.oggetti = dao.listObjects();
		}
		
		//creo mappa di tutti id associati all'oggetto
		if(this.idMap.isEmpty()) {
			for(ArtObject a : this.oggetti) {
				this.idMap.put(a.getId(), a);
			}
		}
	}
	
	//QUESTO METODO VA BENE SE ABBIAMO UN DATABASE PICCOLO (circa20-30 righe)
	/*
	public void buildGraph() {
		
		loadNodes();
		Graphs.addAllVertices(this.graph, oggetti);
		
		for(ArtObject a1: this.oggetti) {
			for(ArtObject a2: this.oggetti) {
				//questo è il peso che dobbiamo mettere nell'arco
				int peso = this.dao.getWeight(a1.getId(), a2.getId());
				//assegno ad ogni arco il suo "peso"
				Graphs.addEdgeWithVertices(this.graph, a1, a2, peso);
			}
		}
	}
	*/
	
	public void buildGraph() {
		
		loadNodes();
		Graphs.addAllVertices(this.graph, oggetti);
		
		//lista che contiene gli id-o1 e id_o2 e il peso dell'arco
		List<edgeModel> allEdge = this.dao.getAllWeights(idMap);
		
		for(edgeModel o : allEdge) {
			Graphs.addEdgeWithVertices(this.graph, o.getSource(), o.getTarget(), o.getPeso());
		}
		
		System.out.println("This graph contains: "+this.graph.vertexSet().size()+" vertici\n");
		System.out.println("This graph contains: "+this.graph.edgeSet().size()+" archi\n");
	}
	
	public boolean isIdInGraph(int id) {
		
		//controllo se id passato non è nullo e se c'è nella lista
		if(this.idMap.get(id)!=null) {
			return true;
		} else {
			return false;
		}
	}
	
	public Integer calcolaConnessa(Integer id) {
		
		//calcolo tutti i vertici connessi
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> iterator = new DepthFirstIterator<>(this.graph, this.idMap.get(id));
		List<ArtObject> compConnessa = new ArrayList<ArtObject>();
		
		while(iterator.hasNext()) {
			compConnessa.add(iterator.next());
		}
		return compConnessa.size();
	}
	
	
}
