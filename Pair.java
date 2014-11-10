/**
 * 
 * Represents a pair of Profiles of two different individuals.
 * Used as a Key in HashMap where the Value is the associated match score of the Profile pair.
 * 
 * /

class Pair {
	public int idA;
	public int idB;

	public Pair() {}

	public Pair(int idA, int idB) {
		this.idA = idA;
		this.idB = idB;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idA;
		result = prime * result + idB;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (idA != other.idA)
			return false;
		if (idB != other.idB)
			return false;
		return true;
	}
	
}
