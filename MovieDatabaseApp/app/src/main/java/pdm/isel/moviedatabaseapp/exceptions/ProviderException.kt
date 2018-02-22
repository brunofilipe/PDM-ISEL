package pdm.isel.moviedatabaseapp.exceptions

class ProviderException : AppException {
    constructor(message:String) : super(message)
    constructor() : super("Error while getting information from Provider !!")
}
