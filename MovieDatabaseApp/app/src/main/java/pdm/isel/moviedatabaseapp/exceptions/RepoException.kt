package pdm.isel.moviedatabaseapp.exceptions

class RepoException : AppException {
    constructor(message:String) : super(message)
    constructor() : super("Error while getting information from local Repository !!")
}
