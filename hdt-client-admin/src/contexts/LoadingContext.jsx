import { createContext, useContext, useState } from "react"

const LoadingContext = createContext(null);

export const LoadingProvider = ({children}) => {
    const [isLoading, setIsLoading] = useState(false);
    const [isError, setIsError] = useState(false);



    return (
        <LoadingContext.Provider value={{isLoading, setIsLoading, isError, setIsError}}>
            {children}
        </LoadingContext.Provider>
    )
}

export const useLoading = () => useContext(LoadingContext);