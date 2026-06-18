import React, { useEffect } from 'react'
import apiClient from '../lib/axios';

const Metrix = () => {

    useEffect(() => {
        apiClient.get('/metrics')
        .then(response => {
            console.log('Metrix data:', response.data);
        })
        .catch(error => {
            console.error('Error fetching metrix data:', error);
        });
    }, []);

    return (
    <div>
      
    </div>
  )
}

export default Metrix
